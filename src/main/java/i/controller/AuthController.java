package i.controller;

import i.dto.AuthenticationRequestDto;
import i.dto.TokenDto;
import i.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> auth(@RequestBody @Valid
                                             AuthenticationRequestDto request) {
        return ResponseEntity.ok(authService.token(request));
    }
}