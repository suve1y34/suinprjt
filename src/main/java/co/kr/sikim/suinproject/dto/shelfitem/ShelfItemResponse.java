package co.kr.sikim.suinproject.dto.shelfitem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShelfItemResponse {
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
    // 메모 공개 여부: 'PRIVATE' | 'PUBLIC'
    private String memoVisibility;

    private String addedDatetime;
    private String modifiedDatetime;
}
