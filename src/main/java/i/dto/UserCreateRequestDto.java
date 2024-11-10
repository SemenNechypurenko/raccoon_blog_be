package i.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDto {
    private Long id;
    @NotBlank(message = "should not be empty")
    private String name;
    @NotBlank(message = "should not be empty")
    private String email;
    @NotBlank(message = "should not be empty")
    private String password;
    private Set<RoleDto> roles = new HashSet<>();
}
