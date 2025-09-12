package co.kr.sikim.suinproject.dto.shelfitem;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ShelfItemAddRequest {
    private Long bookshelfId;

    private Long bookId;

    private String isbn13Code;

    private String title;
    private String author;
    private Integer pages;
    private String publisher;
    private String pubDate;

    private String readingStatus;
    private Integer currentPage;

    private String memo;
    private String memoVisibility;

    private String review;
    private String reviewVisibility;

    private LocalDate startDate;
    private LocalDate endDate;
}
