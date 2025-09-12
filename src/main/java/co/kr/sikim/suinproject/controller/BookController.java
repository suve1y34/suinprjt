package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.dto.book.PublicReviewPageResponse;
import co.kr.sikim.suinproject.dto.book.PublicReviewResponse;
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

    @PostMapping("/reviews/public/list")
    public ApiResponse<List<PublicReviewResponse>> listPublicReview(
            @RequestParam String isbn13Code,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size,
            HttpServletResponse resp
    ) {
        PublicReviewPageResponse page = bookSer.listPublicReviewsByIsbn13(isbn13Code, cursor, size);

        if (page.getNextCursor() != null) {
            resp.setHeader("X-Next-Cursor", String.valueOf(page.getNextCursor()));
        }

        return ApiResponse.ok(page.getItems());
    }
}
