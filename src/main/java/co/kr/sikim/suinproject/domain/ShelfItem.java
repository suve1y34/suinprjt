package co.kr.sikim.suinproject.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ShelfItem {
    private Long shelfBookId;
    private Long bookshelfId;
    private Long bookId;
    private Integer currentPage;
    private String readingStatus;

    private LocalDate startDate;
    private LocalDate endDate;

    private String memo;
    // 메모 공개 여부: 'PRIVATE' | 'PUBLIC'
    private String memoVisibility;

    private String review;
    private String reviewVisibility;

    // Mapper 동적 세팅 제어용
    private Boolean memoChanged;
    private Boolean reviewChanged;
}
