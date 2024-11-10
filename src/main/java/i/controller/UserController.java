package i.controller;

import i.dto.UserCreateRequestDto;
import i.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.http.HttpStatus.CREATED;


@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserCreateRequestDto> save(@Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {
        return new ResponseEntity<>(userService.save(userCreateRequestDto), CREATED);
    }

}
