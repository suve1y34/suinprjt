package co.kr.sikim.suinproject.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMyInfoRequest {

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @NotBlank(message = "국가코드는 필수 입력값입니다.")
    private String countryCode;

    @NotBlank(message = "전화번호는 필수 입력값입니다.")
    private String phone;
}