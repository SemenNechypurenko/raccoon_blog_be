package i.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication request.
 * Contains the username and password provided by the user for authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDto {

    /**
     * The username of the user attempting to authenticate.
     * This field is required and should not be empty or blank.
     */
    @NotBlank(message = "Username should not be empty or blank")
    private String username;

    /**
     * The password of the user attempting to authenticate.
     * This field is required and should not be empty or blank.
     */
    @NotBlank(message = "Password should not be empty or blank")
    private String password;
}
