package i.controller;

import i.dto.PostCreateResponseDto;
import i.dto.PostDto;
import i.dto.PostFileUrlDto;
import i.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;

import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RequestMapping("/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @PostMapping
    public ResponseEntity<PostCreateResponseDto> createPost(
            @RequestPart("title") String title,   // Получаем title как строку
            @RequestPart("content") String content, // Получаем content как строку
            @RequestPart(value = "image", required = false) MultipartFile image, // Получаем файл
            Principal principal) {

        return ResponseEntity.ok(
                service.createPost(title, content, image, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getListOfPosts() {
        return ResponseEntity.ok(service.list(null));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostDto>> getListOfPostsForUser(@PathVariable("username") String username) {
        return ResponseEntity.ok(service.list(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.getPostById(id));
    }

    @GetMapping("/{id}/image-url")
    public ResponseEntity<PostFileUrlDto> getImageUrl(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.getImageUrlByPostId(id));
    }

}
