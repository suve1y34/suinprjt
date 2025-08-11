package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.dto.shelf.BookshelfResponse;
import co.kr.sikim.suinproject.service.BookshelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shelves")
@RequiredArgsConstructor
public class BookShelfController {
    private final BookshelfService bsSer;

    @PostMapping("/detail")
    public BookshelfResponse getBookshelf(@RequestParam Long userId) {
        return bsSer.getShelf(userId);
    }
}
