package co.kr.sikim.suinproject.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {
    private int code;
    private String message;
    private T data;

    // 성공 응답 (data 있음)
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(200, "success", data);
    }

    // 성공 응답 (data 없음)
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(200, "success", null);
    }

    // 실패 응답
    public static <T> CommonResponse<T> fail(int code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}
