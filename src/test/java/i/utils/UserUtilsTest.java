package i.utils;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Description;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static i.utils.UserUtils.getCurrentAuthUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class UserUtilsTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Test
    @Description("Test that getCurrentAuthUser returns the authenticated username when SecurityContext is set")
    void getCurrentAuthUser_ShouldReturnAuthenticatedUserName_WhenSecurityContextIsSet() {
        // Arrange
        MockitoAnnotations.openMocks(this);  // Initialize mocks
        String expectedUsername = "user1";

        // Mock the SecurityContextHolder and Authentication
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(expectedUsername);
        SecurityContextHolder.setContext(securityContext);  // Set the mock context

        // Act
        String actualUsername = getCurrentAuthUser();

        // Assert
        assertEquals(expectedUsername, actualUsername);
    }

    @Test
    @Description("Test that getCurrentAuthUser returns null when no authentication is present in the SecurityContext")
    void getCurrentAuthUser_ShouldReturnNull_WhenNoAuthenticationInContext() {
        // Arrange
        MockitoAnnotations.openMocks(this);  // Initialize mocks
        when(securityContext.getAuthentication()).thenReturn(null); // Simulate no authentication
        SecurityContextHolder.setContext(securityContext);  // Set the mock context

        // Act
        String actualUsername = getCurrentAuthUser();

        // Assert
        assertNull(actualUsername);
    }
}
