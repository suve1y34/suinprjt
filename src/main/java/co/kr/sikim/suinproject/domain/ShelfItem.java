package co.kr.sikim.suinproject.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShelfItem {
    private Long shelfBookId;
    private Long bookshelfId;
    private Long bookId;
    private Integer currentPage;
    private String readingStatus;
}
