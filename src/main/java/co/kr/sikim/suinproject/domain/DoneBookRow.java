package co.kr.sikim.suinproject.domain;

import lombok.Data;

@Data
public class DoneBookRow {
    private String dateStr;       // yyyy-MM-dd
    private Long shelfBookId;
    private Long bookId;
    private String title;
    private String coverImageUrl;
    private String isbn13Code;
}
