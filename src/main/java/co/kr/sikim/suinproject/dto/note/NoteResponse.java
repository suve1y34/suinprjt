package co.kr.sikim.suinproject.dto.note;

import lombok.Data;

@Data
public class NoteResponse {
    private Long noteId;
    private Long bookId;
    private Long userId;
    private String content;
    private Integer rating;
    private String createdDatetime;
    private String modifiedDatetime;
}
