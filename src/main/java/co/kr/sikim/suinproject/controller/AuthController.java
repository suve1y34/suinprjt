package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.dto.auth.AuthLoginRequest;
import co.kr.sikim.suinproject.dto.auth.AuthLoginResponse;
import co.kr.sikim.suinproject.dto.auth.RegisterRequest;
import co.kr.sikim.suinproject.dto.auth.UserResponse;
import co.kr.sikim.suinproject.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authSer;
    public AuthController(AuthService authService) { this.authSer = authService; }

    @PostMapping("/login")
    public ApiResponse<AuthLoginResponse> login(@RequestBody AuthLoginRequest req) {
        return ApiResponse.ok(authSer.login(req.getEmail(), req.getPassword()));
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, Boolean>> logout() {
        return ApiResponse.ok(Map.of("success", true));
    }

    @PostMapping("/resetPw")
    public ApiResponse<Map<String, Boolean>> resetPassword() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null)
            throw new SecurityException("unauthorized");

        Long userId = (auth.getPrincipal() instanceof Long)
                ? (Long) auth.getPrincipal()
                : Long.valueOf(String.valueOf(auth.getPrincipal()));

        authSer.resetPassword(userId);
        return ApiResponse.ok(Map.of("success", true));
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequest req) {
        return ApiResponse.ok(authSer.createUser(req));
    }
}
