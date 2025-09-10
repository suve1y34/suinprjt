package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.dto.book.PublicMemoPageResponse;
import co.kr.sikim.suinproject.dto.book.PublicMemoResponse;
import co.kr.sikim.suinproject.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookSer;

    @PostMapping("/memos/public/list")
    public ApiResponse<List<PublicMemoResponse>> listPublicMemos(
            @RequestParam String isbn13Code,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size,
            HttpServletResponse resp
    ) {
        PublicMemoPageResponse page = bookSer.listPublicMemosByIsbn13(isbn13Code, cursor, size);

        if (page.getNextCursor() != null) {
            resp.setHeader("X-Next-Cursor", String.valueOf(page.getNextCursor()));
        }

        return ApiResponse.ok(page.getItems());
    }
}
