package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.dto.auth.AuthLoginRequest;
import co.kr.sikim.suinproject.dto.auth.AuthLoginResponse;
import co.kr.sikim.suinproject.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public ApiResponse<AuthLoginResponse> login(@RequestBody AuthLoginRequest req) {
        return ApiResponse.ok(authService.login(req.getEmail(), req.getPassword()));
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, Boolean>> logout() {
        return ApiResponse.ok(Map.of("success", true));
    }
}
