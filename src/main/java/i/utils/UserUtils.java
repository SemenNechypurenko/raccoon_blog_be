package i.utils;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {
    public static String getCurrentAuthUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
