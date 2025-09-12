package co.kr.sikim.suinproject.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    private String nickname;
    private String userPhone;
    private Integer goalYearlyCount;
}
