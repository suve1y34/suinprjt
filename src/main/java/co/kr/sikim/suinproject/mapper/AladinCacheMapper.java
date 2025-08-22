package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.AladinCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AladinCacheMapper {
    AladinCache selectAladinCacheByIsbn13Code(@Param("isbn13Code") String isbn13Code);
    boolean existAladinCacheByIsbn13Code(@Param("isbn13Code") String isbn13Code);
    int insertAladinCache(AladinCache cache);
    int updateAladinCache(AladinCache cache);
}
