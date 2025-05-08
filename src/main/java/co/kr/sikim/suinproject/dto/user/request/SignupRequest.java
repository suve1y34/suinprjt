package co.kr.sikim.suinproject.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "국가코드는 필수 입력값입니다.")
    private String countryCode;

    @NotBlank(message = "연락처는 필수 입력값입니다.")
    private String phone;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    private int nickname;
}
