package co.kr.sikim.suinproject.dto.user.request;

@lombok.Getter
public class SignupRequest {
    private String memberId;
    private String password;
    private String name;
    private String email;
    private String countryCode;
    private String phone;
    private int roleNo;
    private int statusNo;
}
