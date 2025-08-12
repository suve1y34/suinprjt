package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemAddRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemDeleteRequest;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemResponse;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemUpdateRequest;
import co.kr.sikim.suinproject.service.ShelfItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelves/books")
@RequiredArgsConstructor
public class ShelfBookController {
    private final ShelfItemService siSer;

    // 목록 조회
    @PostMapping("/list")
    public List<ShelfItemResponse> listShelfItems(@RequestParam Long bookshelfId) {
        return siSer.listShelfItems(bookshelfId);
    }

    // 책 추가
    @PostMapping("/add")
    public ShelfItemResponse createShelfItem(@RequestBody ShelfItemAddRequest req) {
        return siSer.createShelfItem(req);
    }

    // 책 수정
    @PostMapping("/update")
    public ShelfItemResponse updateShelfItem(@RequestBody ShelfItemUpdateRequest req) {
        return siSer.updateShelfItem(req);
    }

    // 책 삭제
    @PostMapping("/remove")
    public void deleteShelfItem(@RequestBody ShelfItemDeleteRequest req) {
        siSer.deleteShelfItem(req);
    }
}
