package i.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @Id
    private String id = UUID.randomUUID().toString();  // Unique identifier for the user

    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Roles cannot be empty")
    private Set<Role> roles = new HashSet<>();

    private LocalDateTime createdAt = LocalDateTime.now();    // Date and time the user was created

    private String confirmationToken;  // Token for email confirmation

    private boolean emailVerified = false;  // Flag indicating if the email is confirmed
}
