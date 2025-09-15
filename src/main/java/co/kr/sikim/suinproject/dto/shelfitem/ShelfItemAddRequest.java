package co.kr.sikim.suinproject.dto.shelfitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ShelfItemAddRequest {
    @NotNull
    private Long bookshelfId;

    private Long bookId;

    @Size(max = 13)
    private String isbn13Code;

    @Pattern(regexp = "PLAN|READING|DONE", message = "상태값 오류")
    private String title;
    private String author;
    private Integer pages;
    private String publisher;
    private String pubDate;

    private String readingStatus;

    @Min(0)
    private Integer currentPage;

    @Size(max = 4000)
    private String memo;
    private String memoVisibility;

    @Size(max = 4000)
    private String review;
    @Pattern(regexp = "PUBLIC|PRIVATE", message = "공개범위 오류")
    private String reviewVisibility;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식 yyyy-MM-dd")
    private String startDate;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식 yyyy-MM-dd")
    private String endDate;
}
