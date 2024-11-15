package i.controller;

import i.dto.PostCreateRequestDto;
import i.dto.PostCreateResponseDto;
import i.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @PostMapping
    public ResponseEntity<PostCreateResponseDto> createPost
            (@RequestBody PostCreateRequestDto postCreateRequestDto) {
        return ResponseEntity.ok(service.createPost(postCreateRequestDto));
    }

}
