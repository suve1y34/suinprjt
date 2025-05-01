package co.kr.sikim.suinproject.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String memberId;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;
}