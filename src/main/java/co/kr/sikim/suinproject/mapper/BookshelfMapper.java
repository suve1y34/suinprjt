package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.Bookshelf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookshelfMapper {
    Bookshelf selectBookshelfById(@Param("userId") Long userId);
    Integer countShelfItems(@Param("userId") Long userId);
    boolean existBookshelfById(@Param("userId") Long userId);
}
