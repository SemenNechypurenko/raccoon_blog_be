package i.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

public class UserUtils {
    public static String getCurrentAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null; // Handle the case where Authentication is null
        }
        return authentication.getName();
    }
}
