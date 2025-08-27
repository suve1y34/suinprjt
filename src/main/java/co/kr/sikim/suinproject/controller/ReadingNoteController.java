package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.dto.note.*;
import co.kr.sikim.suinproject.service.ReadingNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class ReadingNoteController {
    private final ReadingNoteService nSer;

    @PostMapping("/list")
    public ApiResponse<List<NoteResponse>> list(@RequestBody NoteListRequest req) {
        return ApiResponse.ok(nSer.listNotes(req.getBookId()));
    }

    @PostMapping("/create")
    public ApiResponse<NoteResponse> create(@AuthenticationPrincipal Long userId,
                                           @RequestBody NoteCreateRequest req) {
        return ApiResponse.ok(nSer.createNote(userId, req));
    }

    @PostMapping("/update")
    public ApiResponse<NoteResponse> update(@AuthenticationPrincipal Long userId,
                               @RequestBody NoteUpdateRequest req) {
        return ApiResponse.ok(nSer.updateNote(userId, req));
    }

    @PostMapping("/delete")
    public ApiResponse<Map<String, Boolean>> delete(@AuthenticationPrincipal Long userId,
                                                    @RequestBody NoteDeleteRequest req) {
        nSer.deleteNote(userId, req);
        return ApiResponse.ok(Map.of("success", true));
    }
}