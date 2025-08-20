package co.kr.sikim.suinproject.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthLoginResponse {
    private String accessToken;
    private UserResponse user;
}
