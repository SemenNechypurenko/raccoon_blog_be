package i.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateResponseDto {
    private String id;
    private String username;
    private String email;
    private Set<RoleDto> roles;
}
