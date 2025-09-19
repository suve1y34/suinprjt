package co.kr.sikim.suinproject.dto.shelf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDoneResponse {
    private String dateStr;       // yyyy-MM-dd
    private Long shelfBookId;
    private Long bookId;
    private String title;
    private String coverImageUrl;
    private String isbn13Code;
}
