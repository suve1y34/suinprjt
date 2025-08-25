package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.Bookshelf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookshelfMapper {
    Bookshelf selectBookshelfById(@Param("userId") Long userId);
    Integer countShelfItems(@Param("bookshelfId") Long bookshelfId);
    boolean existBookshelfById(@Param("bookshelfId") Long bookshelfId);
}
