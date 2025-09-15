package co.kr.sikim.suinproject.dto.shelfitem;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ShelfItemResponse {
    private Long shelfBookId;
    private Long bookshelfId;
    private Long bookId;

    private String isbn13Code;

    private String title;
    private String author;
    private Integer pages;
    private String coverImageUrl;

    private String startDate;
    private String endDate;

    private Integer currentPage;
    private String readingStatus;

    private String memo;
    private String memoVisibility;

    private String review;
    private String reviewVisibility;

    private String addedDatetime;
    private String modifiedDatetime;
}
