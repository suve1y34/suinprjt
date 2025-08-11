package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {
    // 책 목록 조회
    List<Book> selectBooks(@Param("keyword") String keyword);
    // 책 상세 조회
    Book selectBookById(@Param("bookId") Long bookId);
}
