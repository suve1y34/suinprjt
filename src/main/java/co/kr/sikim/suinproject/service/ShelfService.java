package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.shelf.BookshelfResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemAddRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemDeleteRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemUpdateRequest;

import java.util.List;

public interface ShelfService {
    BookshelfResponse getShelf(Long userId);
    ShelfItemResponse createShelfItem(ShelfItemAddRequest req);
    ShelfItemResponse updateShelfItem(ShelfItemUpdateRequest req);
    ShelfItemResponse updateShelfItemMemo(Long shelfBookId, String memo, Long userId);
    void deleteShelfItem(ShelfItemDeleteRequest req);
    List<ShelfItemResponse> listShelfItems(Long bookShelfId);
}
