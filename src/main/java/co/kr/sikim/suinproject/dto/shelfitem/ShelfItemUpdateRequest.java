package co.kr.sikim.suinproject.dto.shelfitem;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShelfItemUpdateRequest {
    private Long shelfBookId;

    private Integer currentPage;
    // 도서 상태 (null이면 변경x)
    private String readingStatus;
}
