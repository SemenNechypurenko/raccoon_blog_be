package i.service;

import i.dto.CommentCreateRequestDto;
import i.dto.CommentCreateResponseDto;
import i.dto.CommentDto;
import i.model.Comment;
import i.model.Post;
import i.repository.CommentRepository;
import i.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository; // Для проверки существования поста
    private final ModelMapper mapper;

    public CommentCreateResponseDto createComment(CommentCreateRequestDto requestDto, String username) {
        // Проверяем, существует ли пост с указанным postId
        Post post = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post with ID " + requestDto.getPostId() + " does not exist"));

        // Настраиваем маппинг для пропуска поля id
        mapper.typeMap(CommentCreateRequestDto.class, Comment.class).addMappings(m -> {
            m.skip(Comment::setId); // skip mapping id
        });

        // Маппинг DTO -> Entity
        Comment comment = mapper.map(requestDto, Comment.class);

        // Устанавливаем дополнительные поля
        comment.setUsername(username);
        comment.setCreatedAt(LocalDateTime.now());

        // Сохраняем комментарий в базе данных
        comment = commentRepository.save(comment);

        post.getCommentIds().add(comment.getId());
        postRepository.save(post);

        // Конвертируем сохраненную сущность в Response DTO и возвращаем
        return mapper.map(comment, CommentCreateResponseDto.class);
    }

    public List<CommentDto> listCommentsByPostId(String postId) {
        // Получаем все комментарии к заданному посту, конвертируем в DTO и возвращаем
        return commentRepository.findByPostId(postId).stream()
                .map(comment -> mapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());
    }

    public CommentDto getCommentById(String id) {
        return commentRepository.findById(id)
                .map(comment -> mapper.map(comment, CommentDto.class))
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }
}
