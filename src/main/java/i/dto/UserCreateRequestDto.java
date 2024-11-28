package i.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * DTO for creating a new user.
 * This DTO contains the required details for creating a user, including username, email, password, and roles.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDto {

    /**
     * The username of the user.
     * This field is required and should not be empty or blank.
     */
    @NotBlank(message = "Username should not be empty or blank")
    private String username;      // User's username

    /**
     * The email address of the user.
     * This field is required and should not be empty or blank.
     */
    @NotBlank(message = "Email should not be empty or blank")
    private String email;         // User's email address

    /**
     * The password of the user.
     * This field is required and should not be empty or blank.
     */
    @NotBlank(message = "Password should not be empty or blank")
    private String password;      // User's password

    /**
     * The roles assigned to the user.
     * This is an optional field, but it will contain roles such as ADMIN, USER, etc.
     */
    private Set<RoleDto> roles = new HashSet<>();   // User's roles
}
