package co.kr.sikim.suinproject.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String memberId;
    private String name;
    private int roleNo;
}
