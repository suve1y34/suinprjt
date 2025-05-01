package co.kr.sikim.suinproject.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import co.kr.sikim.suinproject.domain.NoCode;

@Mapper
public interface TestMapper {
    @Select("SELECT no_seqno, group_name, code_value, code_name FROM no_code LIMIT 1")
    NoCode getOne();
}
