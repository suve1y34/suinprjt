package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectUserById(@Param("userId") Long userId);
    User selectUserByEmail(@Param("userEmail") String userEmail);
    boolean existsUserByUserEmail(@Param("userEmail") String userEmail);

    int insertUser(User user);
    int updateUser(User user);
    int deleteUserById(@Param("userId") Long userId);
}
