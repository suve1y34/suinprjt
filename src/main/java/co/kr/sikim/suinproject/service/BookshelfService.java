package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.shelf.BookshelfResponse;

public interface BookshelfService {
    BookshelfResponse getShelf(Long userId);
}
