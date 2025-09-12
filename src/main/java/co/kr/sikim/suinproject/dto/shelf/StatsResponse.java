package co.kr.sikim.suinproject.dto.shelf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    private List<StatusRatio> statusRatio; // 상태별 개수
    private List<MonthlyCount> monthly;    // 월별 완료 권수

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class StatusRatio {
        private String label; // 읽기전 | 읽는중 | 다읽음
        private int value;    // 개수
        private String key;   // PLAN | READING | DONE
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class MonthlyCount {
        private String label; // "1월".."12월"
        private int value;    // 개수
        private int month;    // 1..12
    }
}
