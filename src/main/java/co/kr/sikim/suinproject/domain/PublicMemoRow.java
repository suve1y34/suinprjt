package co.kr.sikim.suinproject.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PublicMemoRow {
    private Long shelfBookId;
    private String nickname;
    private String memo;
    private LocalDateTime addedDatetime;
}
