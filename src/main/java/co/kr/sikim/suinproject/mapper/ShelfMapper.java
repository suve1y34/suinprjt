package co.kr.sikim.suinproject.mapper;

import co.kr.sikim.suinproject.domain.Bookshelf;
import co.kr.sikim.suinproject.domain.ShelfItem;
import co.kr.sikim.suinproject.domain.ShelfItemJoinRow;
import co.kr.sikim.suinproject.dto.shelf.ShelfItemSearchCond;
import co.kr.sikim.suinproject.dto.shelf.ShelfItemsListRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ShelfMapper {
    int insertBookshelf(Bookshelf bookshelf); // 책장 생성
    int insertShelfItem(ShelfItem item); // 책 등록
    int updateShelfItem(ShelfItem item); // 책 수정
    int deleteShelfItemById(@Param("shelfBookId") Long shelfBookId); // 책 삭제

    Bookshelf selectBookshelfById(@Param("userId") Long userId); // 책장 조회
    ShelfItemJoinRow selectShelfItemById(@Param("shelfBookId") Long shelfBookId); // 책 상세 조회

    List<ShelfItemJoinRow> selectShelfItemsByShelfId(ShelfItemSearchCond req); // 책 리스트 조회

    List<Map<String, Object>> selectStatusCountsByUserId(@Param("userId") Long userId);
    List<Map<String, Object>> selectMonthlyDoneCountsByUserId(@Param("userId") Long userId,
                                                              @Param("year") Integer year);

    Integer countShelfItems(@Param("bookshelfId") Long bookshelfId);
    int countShelfBooksByStatus(@Param("userId") Long userId,
                                @Param("status") String status);

    boolean existsBookshelfById(@Param("bookshelfId") Long bookshelfId, @Param("bookId") Long bookId); // 책장에 책 존재 여부
    boolean existBookshelfById(@Param("bookshelfId") Long bookshelfId);
    boolean existsBookshelfOwnedByUser(Long bookshelfId, Long userId);
    boolean existsShelfItemOwnedByUser(Long shelfBookId, Long userId);
}
