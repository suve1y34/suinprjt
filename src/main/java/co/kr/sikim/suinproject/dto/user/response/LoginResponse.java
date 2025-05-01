package co.kr.sikim.suinproject.dto.user.response;

@lombok.Getter
@lombok.Setter
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String memberId;
    private String name;
    private int roleNo;
}
