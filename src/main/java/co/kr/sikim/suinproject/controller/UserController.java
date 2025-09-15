package co.kr.sikim.suinproject.controller;

import co.kr.sikim.suinproject.common.ApiResponse;
import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.auth.UserResponse;
import co.kr.sikim.suinproject.dto.auth.UserUpdateRequest;
import co.kr.sikim.suinproject.service.ShelfService;
import co.kr.sikim.suinproject.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Users", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService uSer;
    private final ShelfService sSer;

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Operation(summary = "사용자 정보 조회")
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
        r.setUserPhone(u.getUserPhone());
        r.setGoalYearlyCount(u.getGoalYearlyCount());

        return ApiResponse.ok(r);
    }

    @Operation(summary = "사용자 정보 수정")
    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMe(@Valid @RequestBody UserUpdateRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) throw new SecurityException("unauthorized");
        Long userId = (auth.getPrincipal() instanceof Long) ? (Long) auth.getPrincipal() : Long.valueOf(String.valueOf(auth.getPrincipal()));
        return ApiResponse.ok(uSer.updateUser(userId, req));
    }

    @Operation(summary = "사용자 독서 목표 조회")
    @GetMapping("/goal-progress")
    public ApiResponse<Map<String,Object>> getGoalProgress() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) throw new SecurityException("unauthorized");
        Long userId = (auth.getPrincipal() instanceof Long) ? (Long) auth.getPrincipal() : Long.valueOf(String.valueOf(auth.getPrincipal()));

        User u = uSer.getUser(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));

        int done = sSer.countShelfBooksByStatus(userId, "DONE");
        Integer goal = u.getGoalYearlyCount();

        Map<String,Object> r = new HashMap<>();
        r.put("goal", goal);
        r.put("done", done);
        r.put("progressPercent", (goal != null && goal > 0) ? (int)((done*100.0)/goal) : 0);

        return ApiResponse.ok(r);
    }
}
