package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.dto.BookResponse;
import co.kr.sikim.suinproject.dto.BookSearchRequest;
import co.kr.sikim.suinproject.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookSer;

    // 전체 목록 조회
    @RequestMapping(value = "/list", method = {RequestMethod.POST, RequestMethod.GET})
    public List<BookResponse> listBooks(@RequestParam(required = false) BookSearchRequest req) {
        String keyword = (req != null) ? req.getKeyword() : null;
        return bookSer.listBooks(keyword);
    }

    // 상세 조회
    @PostMapping("/detail")
    public BookResponse getBook(@RequestParam Long bookId) {
        return bookSer.getBook(bookId);
    }
}
