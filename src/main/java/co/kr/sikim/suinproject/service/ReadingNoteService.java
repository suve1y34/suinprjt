package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.dto.note.NoteCreateRequest;
import co.kr.sikim.suinproject.dto.note.NoteDeleteRequest;
import co.kr.sikim.suinproject.dto.note.NoteResponse;
import co.kr.sikim.suinproject.dto.note.NoteUpdateRequest;

import java.util.List;

public interface ReadingNoteService {
    List<NoteResponse> listNotes(Long bookId);
    NoteResponse createNote(Long userId, NoteCreateRequest req);
    NoteResponse updateNote(Long userId, NoteUpdateRequest req);
    void deleteNote(Long userId, NoteDeleteRequest req);
}
