package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.dto.aladin.AladinBookResponse;
import co.kr.sikim.suinproject.dto.aladin.AladinSearchRequest;
import co.kr.sikim.suinproject.service.AladinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Aladin", description = "책 검색 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final AladinService aSer;

    @Operation(summary = "알라딘 책 검색")
    @PostMapping("/aladin")
    public ApiResponse<List<AladinBookResponse>> search(@RequestBody AladinSearchRequest req) {
        List<AladinBookResponse> data = aSer.searchBooks(req);
        return ApiResponse.ok(data);
    }
}
