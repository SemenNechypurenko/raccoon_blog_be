package i.controller;

import i.dto.PostCreateResponseDto;
import i.dto.PostDto;
import i.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

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
        // Передаем данные в сервис, где будет создан объект Post
        PostCreateResponseDto responseDto = service.createPost(title, content, image, principal.getName());

        return ResponseEntity.ok(responseDto);
    }


    @GetMapping
    public ResponseEntity<List<PostDto>> getListOfPosts() {
        return new ResponseEntity<>(service.list(null), OK);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostDto>> getListOfPostsForUser(@PathVariable("username") String username) {
        return new ResponseEntity<>(service.list(username), OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.getPostById(id));
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.getImageByPostId(id));
    }


}
