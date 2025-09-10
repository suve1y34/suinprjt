package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.book.BookResponse;
import co.kr.sikim.suinproject.dto.book.PublicMemoPageResponse;
import co.kr.sikim.suinproject.dto.book.PublicMemoResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> listBooks(String keyword);
    BookResponse getBook(Long bookId);
    PublicMemoPageResponse listPublicMemosByIsbn13(String isbn13, Long cursor, int size);
}
