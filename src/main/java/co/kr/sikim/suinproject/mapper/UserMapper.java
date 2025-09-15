package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    int insertUser(User user);
    int updateUser(User user);
    int deleteUserById(@Param("userId") Long userId);

    User selectUserById(@Param("userId") Long userId);
    User selectUserByEmail(@Param("userEmail") String userEmail);

    boolean existsUserByUserEmail(@Param("userEmail") String userEmail);
    boolean existsUserByNickname(@Param("nickname") String nickname); // 닉네임 존재 여부
    boolean existsUserByNicknameAndNotUserId(@Param("nickname") String nickname, @Param("userId") Long userId); // 사용자 제외 닉네임 존재 여부
}
