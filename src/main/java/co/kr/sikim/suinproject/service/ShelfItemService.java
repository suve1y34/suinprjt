package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemAddRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemDeleteRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemUpdateRequest;

import java.util.List;

public interface ShelfItemService {
    ShelfItemResponse createShelfItem(ShelfItemAddRequest req);
    ShelfItemResponse updateShelfItem(ShelfItemUpdateRequest req);
    void deleteShelfItem(ShelfItemDeleteRequest req);
    List<ShelfItemResponse> listShelfItems(Long bookShelfId);
}
