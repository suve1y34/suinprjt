package co.kr.sikim.suinproject.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String userName;
    private String nickname;
}
