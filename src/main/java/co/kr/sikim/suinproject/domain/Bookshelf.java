package co.kr.sikim.suinproject.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Bookshelf {
    private Long bookshelfId;
    private Long userId;
    private LocalDateTime createdDatetime;
    private LocalDateTime modifiedDatetime;
}
