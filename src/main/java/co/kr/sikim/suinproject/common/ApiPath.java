package co.kr.sikim.suinproject.common;

public class ApiPath {
    public static final String BASE = "/api";

    public static final class User {
        public static final String BASE = "/api/users";
        public static final String LOGIN = "/login";
        public static final String CHECK_ID = "/check-id";
        public static final String CHECK_NICKNAME = "/check-nickname";
        public static final String RESET_PASSWORD = "/reset-password";
        public static final String CHANGE_PASSWORD = "/password";
        public static final String ME = BASE + "/me";
    }
}
