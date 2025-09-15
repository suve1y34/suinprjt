package co.kr.sikim.suinproject.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils(){}

    public static Long currentUserIdOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null)
            throw new SecurityException("unauthorized");
        Object p = auth.getPrincipal();
        if (p instanceof Long) return (Long) p;
        return Long.valueOf(String.valueOf(p));
    }
}
