package co.kr.sikim.suinproject.dto.auth;

import lombok.Data;

@Data
public class AuthLoginRequest {
    private String email;
    private String password;
}
