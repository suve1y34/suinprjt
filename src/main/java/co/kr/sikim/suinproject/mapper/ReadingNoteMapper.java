package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.ReadingNote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReadingNoteMapper {
    // 조회 (목록/단건)
    List<ReadingNote> selectReadingNotesByBookId(@Param("bookId") Long bookId);
    ReadingNote selectReadingNoteById(@Param("noteId") Long noteId);

    // cud
    int insertReadingNote(ReadingNote note);
    int updateReadingNote(ReadingNote note);
    int deleteReadingNoteById(@Param("noteId") Long noteId);

    // 권한 검증
    Long selectNoteOwnerUserId(@Param("noteId") Long noteId);
}
