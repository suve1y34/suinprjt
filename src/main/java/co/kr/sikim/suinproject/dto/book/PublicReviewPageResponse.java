package co.kr.sikim.suinproject.dto.book;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PublicReviewPageResponse {
    private List<PublicReviewResponse> items;
    private Long nextCursor; // 더보기 커서
}
