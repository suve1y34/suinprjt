package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.domain.Book;

public interface BookInternalService {
    Book upsertByIsbn13AndFillPagesIfMissing(
            String isbn13, String title, String author, Integer pages,
            String publisher, String pubDate
    );
}