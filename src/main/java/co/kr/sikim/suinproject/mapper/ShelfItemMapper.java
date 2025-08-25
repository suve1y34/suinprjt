package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.ShelfItem;
import co.kr.sikim.suinproject.domain.ShelfItemJoinRow;
import co.kr.sikim.suinproject.dto.shelfitem.ShelfItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShelfItemMapper {
    int insertShelfItem(ShelfItem item); // 독서 등록
    int updateShelfItem(ShelfItem item); // 독서 수정
    int deleteShelfItemById(@Param("shelfBookId") Long shelfBookId); // 독서 삭제

    boolean existsBookshelfById(@Param("bookshelfId") Long bookshelfId, @Param("bookId") Long bookId); // 책장에 책 존재 여부
    int countShelfItemsByBookshelfId(@Param("bookshelfId") Long bookshelfId); // 책장 책 카운트

    List<ShelfItemJoinRow> selectShelfItemsByShelfId(@Param("bookshelfId") Long bookshelfId); // 책 리스트 조회
    ShelfItemJoinRow selectShelfItemById(@Param("shelfBookId") Long shelfBookId); // 책 상세 조회
}
