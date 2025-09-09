package co.kr.sikim.suinproject.dto.shelf;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShelfItemSearchCond {
    private Long bookshelfId;
    private String status;  // PLAN | READING | DONE
    private Integer year;   // YYYY
    private Integer month;  // 1~12
}
