package co.kr.sikim.suinproject.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;        // 0: OK, 그 외: 에러
    private String message;  // 메시지
    private T data;          // 실제 데이터
    private Map<String, String> errors; // 오류

    /** 성공 */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "OK", data, null);
    }

    /** 실패 (데이터 없음) */
    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null, null);
    }

    /** 검증 실패 (필드 오류 포함) */
    public static <T> ApiResponse<T> validation(String message, Map<String, String> fieldErrors) {
        return new ApiResponse<>(400, message, null, fieldErrors);
    }

    /** 하위호환용: error() 유지 (내부적으로 fail 로 위임) */
    public static <T> ApiResponse<T> error(int code, String message) {
        return fail(code, message);
    }
}
