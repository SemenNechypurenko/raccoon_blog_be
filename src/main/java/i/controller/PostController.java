package i.controller;

import i.dto.PostCreateRequestDto;
import i.dto.PostCreateResponseDto;
import i.dto.PostDto;
import i.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @PostMapping
    public ResponseEntity<PostCreateResponseDto> createPost
            (@RequestBody PostCreateRequestDto postCreateRequestDto, Principal principal) {
        return ResponseEntity.ok(service.createPost(postCreateRequestDto, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getListOfPosts() {
        return new ResponseEntity<>(service.list(), OK);
    }



}
