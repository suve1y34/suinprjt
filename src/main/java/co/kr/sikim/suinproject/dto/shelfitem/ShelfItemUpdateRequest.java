package co.kr.sikim.suinproject.dto.shelfitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ShelfItemUpdateRequest {
    @NotNull
    private Long shelfBookId;

    private Integer currentPage;
    // 도서 상태 (null이면 변경x)
    private String readingStatus;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식 yyyy-MM-dd")
    private String startDate;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식 yyyy-MM-dd")
    private String endDate;
    
    // 메모
    private Boolean memoChanged;
    private String memo;
    private String memoVisibility;

    // 리뷰
    private Boolean reviewChanged;
    private String review;
    @Pattern(regexp = "PUBLIC|PRIVATE", message = "공개범위 오류")
    private String reviewVisibility;
}
