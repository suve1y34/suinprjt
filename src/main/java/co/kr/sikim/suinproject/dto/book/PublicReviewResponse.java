package co.kr.sikim.suinproject.dto.book;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicReviewResponse {
    private Long shelfBookId;
    private String nickname;
    private String addedDatetime; // "yyyy-MM-dd HH:mm:ss"
    private String review;
}
