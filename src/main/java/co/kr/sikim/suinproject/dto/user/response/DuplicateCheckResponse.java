package co.kr.sikim.suinproject.dto.user.response;

@lombok.Getter
@lombok.Setter
public class DuplicateCheckResponse {
    private boolean isDuplicate;

    public DuplicateCheckResponse(boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }
}
