package co.kr.sikim.suinproject.dto.user.response;

import lombok.Getter;

@Getter
public class DuplicateCheckResponse {
    private boolean isDuplicate;

    public DuplicateCheckResponse(boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }
}
