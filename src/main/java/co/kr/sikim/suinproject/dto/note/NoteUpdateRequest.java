package co.kr.sikim.suinproject.dto.note;

import lombok.Data;

@Data
public class NoteUpdateRequest {
    private Long noteId;
    private String content;
    private Integer rating;
}
