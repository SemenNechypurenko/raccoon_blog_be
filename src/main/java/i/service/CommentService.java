package i.service;

import i.dto.CommentCreateRequestDto;
import i.dto.CommentCreateResponseDto;
import i.dto.CommentDto;
import i.exception.MessageOrPostNotFoundException;
import i.model.Comment;
import i.model.Item;
import i.model.Message;
import i.model.Post;
import i.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import i.repository.CommentRepository;
import i.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MessageRepository messageRepository;
    private final ModelMapper mapper;

    public CommentCreateResponseDto createComment(CommentCreateRequestDto requestDto, String username) {
        // Получаем item (Post или Message) по ID
        Item item = getItemById(requestDto.getItemId());

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

        // Добавляем комментарий в список комментариев item и сохраняем item
        item.getCommentIds().add(comment.getId());
        saveItem(item);

        // Конвертируем сохраненную сущность в Response DTO и возвращаем
        return mapper.map(comment, CommentCreateResponseDto.class);
    }

    private Item getItemById(String itemId) {
        return postRepository.findById(itemId).map(post -> (Item) post)
                .orElseGet(() -> messageRepository.findById(itemId).map(message -> (Item) message)
                        .orElseThrow(() -> new MessageOrPostNotFoundException("No Message or Post found with ID " + itemId)));
    }

    private void saveItem(Item item) {
        if (item instanceof Post) {
            postRepository.save((Post) item);
        } else if (item instanceof Message) {
            messageRepository.save((Message) item);
        }
    }

    public List<CommentDto> listCommentsByPostId(String itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(comment -> mapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());
    }

    public CommentDto getCommentById(String id) {
        return commentRepository.findById(id)
                .map(comment -> mapper.map(comment, CommentDto.class))
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }
}
