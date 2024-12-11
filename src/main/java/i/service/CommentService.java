package i.service;

import i.dto.CommentCreateRequestDto;
import i.dto.CommentCreateResponseDto;
import i.dto.CommentDto;
import i.exception.CommentNotFoundException;
import i.exception.MessageOrPostNotFoundException;
import i.model.Comment;
import i.model.Item;
import i.model.Message;
import i.model.Post;
import i.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import i.repository.CommentRepository;
import i.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MessageRepository messageRepository;
    private final ModelMapper mapper;

    /**
     * Creates a comment based on the request DTO and the provided username.
     * @param requestDto The data transfer object containing the comment details.
     * @param username The username of the user creating the comment.
     * @return The created comment's response DTO.
     */
    public CommentCreateResponseDto createComment(CommentCreateRequestDto requestDto, String username) {
        log.debug("Attempting to create comment for item ID: {}", requestDto.getItemId());

        // Fetch the item (Post or Message) by ID
        Item item = getItemById(requestDto.getItemId());
        log.debug("Item found: {}", item);

        // Set up mapping to skip 'id' field
        mapper.typeMap(CommentCreateRequestDto.class, Comment.class).addMappings(m -> {
            m.skip(Comment::setId); // Skip mapping id field
        });

        // Map the DTO to Entity
        Comment comment = mapper.map(requestDto, Comment.class);

        // Set additional fields for the comment
        comment.setUsername(username);
        comment.setCreatedAt(LocalDateTime.now());

        // Save the comment to the database
        comment = commentRepository.save(comment);
        log.debug("Comment saved with ID: {}", comment.getId());

        // Add the comment to the item and save the item
        item.getCommentIds().add(comment.getId());
        saveItem(item);

        // Convert the saved entity to Response DTO and return
        return mapper.map(comment, CommentCreateResponseDto.class);
    }

    /**
     * Retrieves an item (Post or Message) by its ID.
     * @param itemId The ID of the item to fetch.
     * @return The found item (Post or Message).
     * @throws MessageOrPostNotFoundException If no item with the given ID is found.
     */
    private Item getItemById(String itemId) {
        log.debug("Fetching item with ID: {}", itemId);

        return postRepository.findById(itemId).map(post -> (Item) post)
                .orElseGet(() -> messageRepository.findById(itemId).map(message -> (Item) message)
                        .orElseThrow(() -> {
                            log.error("No Message or Post found with ID: {}", itemId);
                            return new MessageOrPostNotFoundException("No Message or Post found with ID " + itemId);
                        }));
    }

    /**
     * Saves an item (either Post or Message) to the database.
     * @param item The item to save.
     */
    private void saveItem(Item item) {
        if (item instanceof Post) {
            log.debug("Saving Post item with ID: {}", item.getId());
            postRepository.save((Post) item);
        } else if (item instanceof Message) {
            log.debug("Saving Message item with ID: {}", item.getId());
            messageRepository.save((Message) item);
        }
    }

    /**
     * Retrieves a list of comments for a given Post ID.
     * @param itemId The ID of the Post to fetch comments for.
     * @return A list of Comment DTOs.
     */
    public List<CommentDto> listCommentsByPostId(String itemId) {
        log.debug("Listing comments for Post ID: {}", itemId);

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(comment -> mapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());

        log.debug("Found {} comments for Post ID: {}", comments.size(), itemId);
        return comments;
    }

    /**
     * Retrieves a comment by its ID.
     * @param id The ID of the comment to retrieve.
     * @return The Comment DTO.
     * @throws CommentNotFoundException If the comment with the given ID is not found.
     */
    public CommentDto getCommentById(String id) {
        log.debug("Fetching comment with ID: {}", id);

        return commentRepository.findById(id)
                .map(comment -> mapper.map(comment, CommentDto.class))
                .orElseThrow(() -> {
                    log.error("Comment not found with ID: {}", id);
                    return new CommentNotFoundException("Comment not found");
                });
    }
}
