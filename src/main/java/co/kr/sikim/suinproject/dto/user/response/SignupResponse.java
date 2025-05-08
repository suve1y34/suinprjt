package co.kr.sikim.suinproject.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {
    private Long memSeqno;
    private String memberId;
}