package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.Book;
import co.kr.sikim.suinproject.dto.BookResponse;
import co.kr.sikim.suinproject.mapper.BookMapper;
import co.kr.sikim.suinproject.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<BookResponse> listBooks(String keyword) {
        String word = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        return bookMapper.selectBooks(word).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public BookResponse getBookById(Long bookId) {
        Book book = bookMapper.selectBookById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("book not found: " + bookId);
        }
        return toDto(book);
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
