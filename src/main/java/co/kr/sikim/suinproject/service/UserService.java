package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.dto.auth.UserResponse;
import co.kr.sikim.suinproject.dto.auth.UserUpdateRequest;

import java.util.Optional;

public interface UserService {
    Optional<User> getUser(Long userId);
    Optional<User> getUserByEmail(String userEmail);
    boolean checkDuplicatedUserEmail(String userEmail);

    UserResponse updateUser(Long userId, UserUpdateRequest req);
}
