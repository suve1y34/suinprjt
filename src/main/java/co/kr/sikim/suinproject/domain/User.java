package co.kr.sikim.suinproject.domain;

import java.time.LocalDateTime;

@lombok.Getter
@lombok.Setter
public class Member {
    private Long memSeqno;
    private String memberId;
    private String password;
    private String name;
    private String email;
    private String countryCode;
    private String phone;
    private int roleNo;
    private int statusNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
