package i.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDto {
    private UserCreateResponseDto user;
    private String token;
}
