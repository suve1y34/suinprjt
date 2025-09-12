package co.kr.sikim.suinproject.dto.shelfitem;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ShelfItemUpdateRequest {
    private Long shelfBookId;

    private Integer currentPage;
    // 도서 상태 (null이면 변경x)
    private String readingStatus;

    private LocalDate startDate;
    private LocalDate endDate;
    
    // 메모
    private Boolean memoChanged;
    private String memo;
    private String memoVisibility;

    // 리뷰
    private Boolean reviewChanged;
    private String review;
    private String reviewVisibility;
}
