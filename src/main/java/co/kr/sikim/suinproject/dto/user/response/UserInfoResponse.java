package co.kr.sikim.suinproject.dto.user.response;

@lombok.Getter
@lombok.Setter
public class UserInfoResponse {
    private Long memSeqno;
    private String memberId;
    private String name;
    private String email;
    private String phone;
    private int roleNo;
    private int statusNo;
}
