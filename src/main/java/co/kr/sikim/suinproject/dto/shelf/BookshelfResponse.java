package co.kr.sikim.suinproject.dto.shelf;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookshelfResponse {
    private Long bookshelfId;
    private Long userId;
    private Integer itemCount;
    private String createdDatetime;
}
