package co.kr.sikim.suinproject.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReadingNote {
    private Long noteId;
    private Long bookId;
    private Long userId;
    private String content;
    private Integer rating;
    private LocalDateTime createdDatetime;
    private LocalDateTime modifiedDatetime;
}
