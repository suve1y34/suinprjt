package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.book.BookResponse;
import co.kr.sikim.suinproject.dto.book.PublicReviewPageResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> listBooks(String keyword);
    BookResponse getBook(Long bookId);
    PublicReviewPageResponse listPublicReviewsByIsbn13(String isbn13, Long cursor, int size);
}
