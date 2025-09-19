package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.shelf.BookshelfResponse;
import co.kr.sikim.suinproject.dto.shelf.CalendarDoneResponse;
import co.kr.sikim.suinproject.dto.shelf.ShelfItemSearchCond;
import co.kr.sikim.suinproject.dto.shelf.StatsResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemAddRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemDeleteRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemUpdateRequest;

import java.util.List;

public interface ShelfService {
    ShelfItemResponse createShelfItem(ShelfItemAddRequest req); // 책 추가

    List<ShelfItemResponse> listShelfItems(ShelfItemSearchCond cond);
    BookshelfResponse getShelf(Long userId);
    int countShelfBooksByStatus(Long userId, String status);
    StatsResponse getShelfStats(Long userId, Integer year);

    ShelfItemResponse updateShelfItem(ShelfItemUpdateRequest req);
    void deleteShelfItem(ShelfItemDeleteRequest req);

    List<CalendarDoneResponse> getMonthlyDoneCovers(Long userId, Integer year, Integer month);
}
