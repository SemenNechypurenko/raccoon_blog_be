package i.controller;

import i.dto.CommentCreateRequestDto;
import i.dto.CommentCreateResponseDto;
import i.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentCreateResponseDto> createComment(@RequestBody CommentCreateRequestDto commentDto) {
        return ResponseEntity.ok(commentService.addComment(commentDto));
    }
}
