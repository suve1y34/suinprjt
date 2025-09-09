package co.kr.sikim.suinproject.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShelfItemJoinRow {
    private Long shelfBookId;
    private Long bookshelfId;
    private Long bookId;
    private String title;
    private String author;
    private Integer pages;
    private String coverImageUrl;
    private Integer currentPage;
    private String readingStatus;
    private String memo;
    private LocalDateTime addedDatetime;  // DATE_FORMAT으로 문자열 확정
    private LocalDateTime modifiedDatetime;
}
