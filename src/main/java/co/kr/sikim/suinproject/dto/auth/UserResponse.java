package co.kr.sikim.suinproject.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String userName;
    private String email;
    private String nickname;
}
