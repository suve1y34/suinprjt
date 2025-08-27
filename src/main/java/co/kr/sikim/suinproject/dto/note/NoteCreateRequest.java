package co.kr.sikim.suinproject.dto.note;

import lombok.Data;

@Data
public class NoteCreateRequest {
    private Long bookId;
    private String content;
    private Integer rating;
}
