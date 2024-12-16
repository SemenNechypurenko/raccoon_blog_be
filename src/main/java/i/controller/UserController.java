package i.controller;

import i.dto.UserCreateRequestDto;
import i.dto.UserDto;
import i.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> save(@Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {
        return new ResponseEntity<>(userService.save(userCreateRequestDto), CREATED);
    }

    // Handles the email confirmation request based on the token
    @GetMapping("/confirm-email")
    public ResponseEntity<Void> confirmEmail(@RequestParam String token) {
        userService.confirmEmail(token);
        return ResponseEntity.ok().build();
    }

    // Handles the email confirmation request based on the token
    @GetMapping
    public ResponseEntity<List<UserDto>> list() {
        return new ResponseEntity<>(userService.list(), OK);
    }

    @GetMapping("/{substring}")
    public ResponseEntity<List<UserDto>> listContainsString (@PathVariable String substring) {
        return new ResponseEntity<>(userService.getUsernamesListBySubstring(substring), OK);
    }
}
