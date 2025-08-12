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
    private String addedDatetime;
}
