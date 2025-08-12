package co.kr.sikim.suinproject.dto.shelfitem;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShelfItemAddRequest {
    private Long bookshelfId;
    private Long bookId;
    private BigDecimal spineWidth;
}
