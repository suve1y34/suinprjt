package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.dto.shelf.BookshelfResponse;
import co.kr.sikim.suinproject.dto.shelf.ShelfItemSearchCond;
import co.kr.sikim.suinproject.dto.shelf.ShelfItemsListRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemAddRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemDeleteRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemUpdateRequest;
import co.kr.sikim.suinproject.service.ShelfService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shelves/books")
@RequiredArgsConstructor
public class ShelfController {

    private final ShelfService siSer;

    @PostMapping("/me")
    public ApiResponse<BookshelfResponse> getBookshelf(@RequestParam Long userId) {
        return ApiResponse.ok(siSer.getShelf(userId));
    }

    // 목록 조회
    @PostMapping("/list")
    public ApiResponse<List<ShelfItemResponse>> listShelfItems(@RequestBody ShelfItemsListRequest req) {
        ShelfItemSearchCond cond = new ShelfItemSearchCond();
        cond.setBookshelfId(req.getBookshelfId());
        cond.setStatus(req.getStatus());
        cond.setYear(req.getYear());
        cond.setMonth(req.getMonth());
        return ApiResponse.ok(siSer.listShelfItems(cond));
    }

    // 책 추가
    @PostMapping("/add")
    public ApiResponse<ShelfItemResponse> createShelfItem(@RequestBody ShelfItemAddRequest req) {
        System.out.println(req.getBookshelfId());
        return ApiResponse.ok(siSer.createShelfItem(req));
    }

    // 책 수정
    @PostMapping("/update")
    public ApiResponse<ShelfItemResponse> updateShelfItem(@RequestBody ShelfItemUpdateRequest req) {
        return ApiResponse.ok(siSer.updateShelfItem(req));
    }

    // 책 삭제
    @PostMapping("/remove")
    public ApiResponse<Map<String, Boolean>> deleteShelfItem(@RequestBody ShelfItemDeleteRequest req) {
        siSer.deleteShelfItem(req);
        return ApiResponse.ok(Map.of("success", true));
    }
}
