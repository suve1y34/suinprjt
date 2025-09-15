package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.Book;
import co.kr.sikim.suinproject.domain.PublicReviewRow;
import co.kr.sikim.suinproject.dto.book.BookResponse;
import co.kr.sikim.suinproject.dto.book.PublicReviewPageResponse;
import co.kr.sikim.suinproject.dto.book.PublicReviewResponse;
import co.kr.sikim.suinproject.mapper.BookMapper;
import co.kr.sikim.suinproject.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookMapper bMapper;
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<BookResponse> listBooks(String keyword) {
        String word = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        return bMapper.selectBooks(word).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public BookResponse getBook(Long bookId) {
        Book book = bMapper.selectBookById(bookId);
        System.out.println(book.getBookId());
        if (book == null) {
            throw new IllegalArgumentException("book not found: " + bookId);
        }
        return toDto(book);
    }

    @Override
    public PublicReviewPageResponse listPublicReviewsByIsbn13(String isbn13, Long cursor, int size) {
        int pageSize = Math.max(1, Math.min(size, 50)); // 안전 가드(1~50)
        List<PublicReviewRow> rows = bMapper.selectPublicReviewsByIsbn13(isbn13, cursor, pageSize);

        List<PublicReviewResponse> items = rows.stream().map(r -> {
            PublicReviewResponse d = new PublicReviewResponse();
            d.setNickname(r.getNickname());
            d.setReview(r.getReview());
            d.setAddedDatetime(r.getAddedDatetime()
                    != null ? r.getAddedDatetime().format(DT) : null);
            return d;
        }).toList();

        Long nextCursor = (items.size() == pageSize)
                ? items.get(items.size() - 1).getShelfBookId()
                : null;

        var page = new PublicReviewPageResponse();
        page.setItems(items);
        page.setNextCursor(nextCursor);
        return page;
    }

    private BookResponse toDto(Book b) {
        BookResponse dto = new BookResponse();
        dto.setBookId(b.getBookId());
        dto.setIsbn13Code(b.getIsbn13Code());
        dto.setTitle(b.getTitle());
        dto.setAuthor(b.getAuthor());
        dto.setPages(b.getPages());
        dto.setPublisher(b.getPublisher());
        dto.setPubDate(b.getPubDate() != null ? b.getPubDate().format(DT) : null);
        return dto;
    }
}
