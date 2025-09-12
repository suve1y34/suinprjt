package co.kr.sikim.suinproject.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublicReviewRow {
    private Long shelfBookId;
    private String nickname;
    private String review;
    private LocalDateTime addedDatetime;
}
