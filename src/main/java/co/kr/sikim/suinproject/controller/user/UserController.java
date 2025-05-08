package co.kr.sikim.suinproject.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.kr.sikim.suinproject.common.CommonResponse;
import co.kr.sikim.suinproject.dto.user.request.ChangePasswordRequest;
import co.kr.sikim.suinproject.dto.user.request.LoginRequest;
import co.kr.sikim.suinproject.dto.user.request.ResetPasswordRequest;
import co.kr.sikim.suinproject.dto.user.request.SignupRequest;
import co.kr.sikim.suinproject.dto.user.request.UpdateMyInfoRequest;
import co.kr.sikim.suinproject.dto.user.response.DuplicateCheckResponse;
import co.kr.sikim.suinproject.dto.user.response.LoginResponse;
import co.kr.sikim.suinproject.dto.user.response.UserInfoResponse;
import co.kr.sikim.suinproject.service.user.UserService;
import co.kr.sikim.suinproject.service.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserServiceImpl uSer;

    @PostMapping("")
    public CommonResponse<Void> signup(@RequestBody SignupRequest request) {
        uSer.sinupUser(request);
        return CommonResponse.success();
    }

    @PostMapping("/login")
    public CommonResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        uSer.loginUser(request);
        return CommonResponse.success(uSer.loginUser(request));
    }

    @PostMapping("/check-id")
    public CommonResponse<DuplicateCheckResponse> checkIdCuplicate(@RequestParam String memberId) {
        return CommonResponse.success(uSer.isIdDuplicate(memberId));
    }

    @PostMapping("/check-nickname")
    public CommonResponse<DuplicateCheckResponse> checkNicknameDuplicate(@RequestParam String nickname) {
        return CommonResponse.success(uSer.isNicknameDuplicate(nickname));
    }
    
    @PostMapping("/reset-password")
    public CommonResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        uSer.resetPassword(request);
        return CommonResponse.success();
    }

    @PostMapping("/password")
    public CommonResponse<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        uSer.changePassword(request);
        return CommonResponse.success();
    }

    @PostMapping("/me")
    public CommonResponse<UserInfoResponse> getMyInfo(@PathVariable String memberId) {
        return CommonResponse.success(uSer.getUserInfo(memberId));
    }

    @PutMapping("/me")
    public CommonResponse<UserInfoResponse> updateMyInfo(@PathVariable String memberId,
                                                         @RequestBody UpdateMyInfoRequest request) {
        return CommonResponse.success(uSer.updateUserInfo(memberId, request));
    }

    @DeleteMapping("/me")
    public CommonResponse<Void> withdraw(@PathVariable String memberId) {
        uSer.withdrawUser(memberId);
        return CommonResponse.success();
    }
}
