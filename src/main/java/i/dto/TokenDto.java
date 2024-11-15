package i.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDto {
    private UserCreateRequestDto user;
    private String token;
}
