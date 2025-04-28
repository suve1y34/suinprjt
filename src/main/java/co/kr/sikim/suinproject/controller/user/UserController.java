package co.kr.sikim.suinproject.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.sikim.suinproject.dto.user.request.UserSignupRequest;
import co.kr.sikim.suinproject.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService uSer;

    @PostMapping("/signup")
    public String createUser(@RequestBody UserSignupRequest request) {
        uSer.handleCreateUser(request);
        return "회원가입 성공";
    }
}
