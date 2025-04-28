package co.kr.sikim.suinproject.mapper;

import org.apache.ibatis.annotations.Mapper;

import co.kr.sikim.suinproject.domain.Member;

@Mapper
public interface MemberMapper {
    int insertUser(Member member);
}
