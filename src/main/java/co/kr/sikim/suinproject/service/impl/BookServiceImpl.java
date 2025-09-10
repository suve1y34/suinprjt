package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.Book;
import co.kr.sikim.suinproject.domain.PublicMemoRow;
import co.kr.sikim.suinproject.dto.book.BookResponse;
import co.kr.sikim.suinproject.dto.book.PublicMemoPageResponse;
import co.kr.sikim.suinproject.dto.book.PublicMemoResponse;
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
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
    public PublicMemoPageResponse listPublicMemosByIsbn13(String isbn13, Long cursor, int size) {
        int pageSize = Math.max(1, Math.min(size, 50)); // 안전 가드(1~50)
        List<PublicMemoRow> rows = bMapper.selectPublicMemosByIsbn13(isbn13, cursor, pageSize);

        List<PublicMemoResponse> items = rows.stream().map(r -> {
            PublicMemoResponse d = new PublicMemoResponse();
            d.setNickname(r.getNickname());
            d.setMemo(r.getMemo());
//            d.setAddedDatetime(r.getAddedDatetime()
//                    != null ? r.getAddedDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
            return d;
        }).toList();

        Long nextCursor = (items.size() == pageSize)
                ? items.get(items.size() - 1).getShelfBookId()
                : null;

        var page = new PublicMemoPageResponse();
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
        dto.setPubDate(b.getPubDate() != null ? b.getPubDate().format(DATE) : null);
        return dto;
    }
}
