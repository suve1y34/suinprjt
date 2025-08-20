package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.domain.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUser(Long userId);
    Optional<User> getUserByEmail(String userEmail);
    boolean checkDuplicatedUserEmail(String userEmail);
}
