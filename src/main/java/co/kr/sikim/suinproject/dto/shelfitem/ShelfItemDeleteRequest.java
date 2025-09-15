package co.kr.sikim.suinproject.dto.shelfitem;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShelfItemDeleteRequest {
    @NotNull
    private Long shelfBookId;
}
