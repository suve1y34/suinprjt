package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.BookResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> listBooks(String keyword);
    BookResponse getBook(Long bookId);
}
