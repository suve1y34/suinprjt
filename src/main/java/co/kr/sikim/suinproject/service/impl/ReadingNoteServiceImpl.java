package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.ReadingNote;
import co.kr.sikim.suinproject.dto.note.NoteCreateRequest;
import co.kr.sikim.suinproject.dto.note.NoteDeleteRequest;
import co.kr.sikim.suinproject.dto.note.NoteResponse;
import co.kr.sikim.suinproject.dto.note.NoteUpdateRequest;
import co.kr.sikim.suinproject.mapper.ReadingNoteMapper;
import co.kr.sikim.suinproject.service.ReadingNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReadingNoteServiceImpl implements ReadingNoteService {

    private final ReadingNoteMapper nMapper;
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<NoteResponse> listNotes(Long bookId) {
        return nMapper.selectReadingNotesByBookId(bookId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public NoteResponse createNote(Long userId, NoteCreateRequest req) {
        validateRating(req.getRating());
        ReadingNote n = new ReadingNote();
        n.setBookId(req.getBookId());
        n.setUserId(userId);
        n.setContent(safeText(req.getContent()));
        n.setRating(req.getRating());
        nMapper.insertReadingNote(n);
        return toResponse(nMapper.selectReadingNoteById(n.getNoteId()));
    }

    @Override
    public NoteResponse updateNote(Long userId, NoteUpdateRequest req) {
        Long owner = nMapper.selectNoteOwnerUserId(req.getNoteId());
        if (!Objects.equals(owner, userId)) throw new AccessDeniedException("Forbidden");
        validateRating(req.getRating());

        ReadingNote n = new ReadingNote();
        n.setNoteId(req.getNoteId());
        n.setContent(safeText(req.getContent()));
        n.setRating(req.getRating());
        nMapper.updateReadingNote(n);

        return toResponse(nMapper.selectReadingNoteById(req.getNoteId()));
    }

    @Transactional
    @Override
    public void deleteNote(Long userId, NoteDeleteRequest req) {
        Long owner = nMapper.selectNoteOwnerUserId(req.getNoteId());
        if (!Objects.equals(owner, userId)) throw new AccessDeniedException("Forbidden");
        nMapper.deleteReadingNoteById(req.getNoteId());
    }

    private NoteResponse toResponse(ReadingNote n) {
        NoteResponse r = new NoteResponse();
        r.setNoteId(n.getNoteId());
        r.setBookId(n.getBookId());
        r.setUserId(n.getUserId());
        r.setContent(n.getContent());
        r.setRating(n.getRating());
        r.setCreatedDatetime(n.getCreatedDatetime() != null ? n.getCreatedDatetime().format(DT) : null);
        r.setModifiedDatetime(n.getModifiedDatetime() != null ? n.getModifiedDatetime().format(DT) : null);
        return r;
    }

    private void validateRating(Integer rating) {
        if (rating == null) return;
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating must be 1~5 or null");
        }
    }

    private String safeText(String s) {
        return s == null ? "" : s;
    }
}
