package co.kr.sikim.suinproject.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String userName;
    private String email;
    private String nickname;
    private String userPhone;
    private Integer goalYearlyCount;
    private String createdDatetime;
    private String modifiedDatetime;
}
