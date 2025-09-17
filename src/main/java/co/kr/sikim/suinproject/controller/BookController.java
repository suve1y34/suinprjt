package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.dto.book.PublicReviewPageResponse;
import co.kr.sikim.suinproject.dto.book.PublicReviewResponse;
import co.kr.sikim.suinproject.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Books", description = "책 정보 API")
@RestController
//@RequestMapping("/api/books")
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookSer;

    @Operation(summary = "공개 리뷰 조회")
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
