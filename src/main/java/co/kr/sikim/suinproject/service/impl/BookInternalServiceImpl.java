package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.Book;
import co.kr.sikim.suinproject.external.AladinClient;
import co.kr.sikim.suinproject.mapper.AladinCacheMapper;
import co.kr.sikim.suinproject.mapper.BookMapper;
import co.kr.sikim.suinproject.service.BookInternalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class BookInternalServiceImpl implements BookInternalService {

    private final BookMapper bMapper;
    private final AladinClient aladinClient;
    private final AladinCacheMapper cMapper;

    public BookInternalServiceImpl(BookMapper bMapper,
                                   AladinClient aladinClient,
                                   AladinCacheMapper cMapper) {
        this.bMapper = bMapper;
        this.aladinClient = aladinClient;
        this.cMapper = cMapper;
    }

    @Transactional
    @Override
    public Book upsertByIsbn13AndFillPagesIfMissing(
            String isbn13, String title, String author, Integer pages,
            String publisher, String pubDate
    ) {
        Book b = bMapper.selectBookByIsbn13Code(isbn13);

        if (b == null) {
            b = new Book();
            b.setIsbn13Code(isbn13);
            b.setTitle(title);
            b.setAuthor(author);
            b.setPages(pages);
            b.setPublisher(publisher);
            b.setPubDate(pubDate != null ? dateFormat(pubDate) : null);

            // INSERT + 생성키 바인딩
            bMapper.insertBook(b); // useGeneratedKeys=true, keyProperty="bookId", keyColumn="book_id"
        } else {
            boolean dirty = false;
            if (b.getPages() == null && pages != null) { b.setPages(pages); dirty = true; }
            if (title != null && !title.isBlank())      { b.setTitle(title); dirty = true; }
            if (author != null && !author.isBlank())     { b.setAuthor(author); dirty = true; }
            if (publisher != null && !publisher.isBlank()){ b.setPublisher(publisher); dirty = true; }
            if (pubDate != null && !pubDate.isBlank())   { b.setPubDate(dateFormat(pubDate)); dirty = true; }
            if (dirty) bMapper.updateBook(b);
        }

        boolean needPage = (b.getPages() == null);
        boolean needCover = (b.getCoverImageUrl() == null || b.getCoverImageUrl().isBlank());
        if (needPage || needCover) {
            String raw = aladinClient.lookupRawByIsbn13(isbn13);
            cMapper.upsertAladinCache(isbn13, raw);

            if (needPage) {
                Integer itemPage = aladinClient.extractItemPageFromLookup(raw);
                if (itemPage != null) {
                    b.setPages(itemPage);
                }
            }
            if (needCover) {
                String coverUrl = aladinClient.extractCoverUrlFromLookup(raw);
                if (coverUrl != null && !coverUrl.isBlank()) {
                    b.setCoverImageUrl(coverUrl);
                }
            }

            // 둘 중 하나라도 채워졌으면 업데이트
            if ((needPage && b.getPages() != null) || (needCover && b.getCoverImageUrl() != null && !b.getCoverImageUrl().isBlank())) {
                bMapper.updateBook(b);
            }
        }

        return bMapper.selectBookByIsbn13Code(isbn13);
    }

    private LocalDate dateFormat(String date) {
        LocalDate format = LocalDate.parse(date);
        return format;
    }
}
