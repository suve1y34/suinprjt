package co.kr.sikim.suinproject.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long userId;
    private String userEmail;
    private String userPassword;
    private String userName;
    private String nickname;
    private LocalDateTime createdDatetime;
    private LocalDateTime modifiedDatetime;
}
