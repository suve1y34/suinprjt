package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.dto.shelf.BookshelfResponse;
import co.kr.sikim.suinproject.dto.shelf.ShelfItemSearchCond;
import co.kr.sikim.suinproject.dto.shelf.ShelfItemsListRequest;
import co.kr.sikim.suinproject.dto.shelf.StatsResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemAddRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemDeleteRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemUpdateRequest;
import co.kr.sikim.suinproject.service.ShelfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Shelves", description = "책장 정보 API")
@RestController
//@RequestMapping("/api/shelves/books")
@RequestMapping("/shelves/books")
@RequiredArgsConstructor
public class ShelfController {

    private final ShelfService siSer;

    // 책 추가
    @Operation(summary = "책 추가")
    @PostMapping("/add")
    public ApiResponse<ShelfItemResponse> createShelfItem(@Valid @RequestBody ShelfItemAddRequest req) {
        System.out.println(req.getBookshelfId());
        return ApiResponse.ok(siSer.createShelfItem(req));
    }

    @Operation(summary = "책장 정보 조회")
    @PostMapping("/me")
    public ApiResponse<BookshelfResponse> getBookshelf(@RequestParam Long userId) {
        return ApiResponse.ok(siSer.getShelf(userId));
    }

    // 목록 조회
    @Operation(summary = "책장의 책 정보 조회")
    @PostMapping("/list")
    public ApiResponse<List<ShelfItemResponse>> listShelfItems(@RequestBody ShelfItemsListRequest req) {
        String keyword = req.getKeyword();

        ShelfItemSearchCond cond = new ShelfItemSearchCond();
        cond.setBookshelfId(req.getBookshelfId());
        cond.setStatus(req.getStatus());
        cond.setYear(req.getYear());
        cond.setMonth(req.getMonth());
        cond.setKeyword((keyword == null || keyword.isBlank()) ? null : keyword.trim());
        cond.setSort(req.getSort());
        cond.setOrder(req.getOrder());
        return ApiResponse.ok(siSer.listShelfItems(cond));
    }

    // 책 수정
    @Operation(summary = "책 수정")
    @PostMapping("/update")
    public ApiResponse<ShelfItemResponse> updateShelfItem(@Valid @RequestBody ShelfItemUpdateRequest req) {
        return ApiResponse.ok(siSer.updateShelfItem(req));
    }

    // 책 삭제
    @Operation(summary = "책 삭제")
    @PostMapping("/remove")
    public ApiResponse<Map<String, Boolean>> deleteShelfItem(@Valid @RequestBody ShelfItemDeleteRequest req) {
        siSer.deleteShelfItem(req);
        return ApiResponse.ok(Map.of("success", true));
    }

    @Operation(summary = "책장 통계 조회")
    @GetMapping("/stats")
    public ApiResponse<StatsResponse> getStats(@RequestParam(value = "year", required = false) Integer year) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) throw new SecurityException("unauthorized");
        Long userId = (auth.getPrincipal() instanceof Long) ? (Long) auth.getPrincipal()
                : Long.valueOf(String.valueOf(auth.getPrincipal()));

        return ApiResponse.ok(siSer.getShelfStats(userId, year));
    }
}
