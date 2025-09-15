package co.kr.sikim.suinproject.dto.auth;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    @Size(min = 2, max = 16, message = "닉네임은 2~16자")
    @Pattern(regexp = "^[A-Za-z0-9가-힣_\\-\\s]+$", message = "허용되지 않는 문자")
    private String nickname;

    @Pattern(regexp = "^(01\\d-?\\d{3,4}-?\\d{4})?$", message = "연락처 형식 오류")
    private String userPhone;

    @Min(value = 0, message = "0 이상")
    private Integer goalYearlyCount;
}
