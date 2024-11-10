package i.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleDto {
    private String id;
    @NotNull(message = "should not be empty")
    private String name;
}