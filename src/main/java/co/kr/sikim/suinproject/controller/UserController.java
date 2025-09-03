package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.auth.UserResponse;
import co.kr.sikim.suinproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService uSer;
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/me")
    public ApiResponse<UserResponse> getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null)
            throw new SecurityException("unauthorized");

        Object p = auth.getPrincipal();
        Long userId = (p instanceof Long) ? (Long)p : Long.valueOf(String.valueOf(p));

        User u = uSer.getUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));

        UserResponse r = new UserResponse();
        r.setUserId(u.getUserId());
        r.setEmail(u.getUserEmail());
        r.setUserName(u.getUserName());
        r.setNickname(u.getNickname());

        return ApiResponse.ok(r);
    }
}
