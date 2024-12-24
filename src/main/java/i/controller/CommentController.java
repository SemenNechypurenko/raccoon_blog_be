package i.controller;

import i.dto.CommentCreateRequestDto;
import i.dto.CommentCreateResponseDto;
import i.dto.CommentDto;
import i.service.CommentService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static i.utils.UserUtils.getCurrentAuthUser;
import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/comments")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping
    public ResponseEntity<CommentCreateResponseDto> createComment(
            @RequestBody CommentCreateRequestDto requestDto) {
        return ResponseEntity.ok(service.createComment(requestDto, getCurrentAuthUser()));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable("postId") String postId) {
        return new ResponseEntity<>(service.listCommentsByPostId(postId), OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable("id") String id) {
        return ResponseEntity.ok(service.getCommentById(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentDto>> getListOfComments(@PathVariable("username") String userId) {
        return ResponseEntity.ok(service.getCommentForUserByUserId(userId));
    }
}
