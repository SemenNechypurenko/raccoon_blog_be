package i.service;

import i.dto.PostCreateResponseDto;
import i.dto.PostDto;
import i.dto.PostFileUrlDto;
import i.model.Post;
import i.repository.PostRepository;
import i.fileStorageClient.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing posts.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository repository;
    private final ModelMapper mapper;
    private final FileStorage fileStorage;

    /**
     * Creates a new post, optionally with an image.
     *
     * @param title    the title of the post
     * @param content  the content of the post
     * @param image    the image file (optional)
     * @param username the username of the author
     * @return a DTO representing the created post
     */
    public PostCreateResponseDto createPost(String title, String content, MultipartFile image, String username) {
        log.debug("Creating a new post for user: {}", username);

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUsername(username);

        // Upload image if provided and set the image URL
        if (image != null) {
            log.debug("Uploading image for post: {}", title);
            String imageUrl = fileStorage.uploadImage(image).block();
            post.setImageUrl(imageUrl);
            log.info("Image uploaded successfully with URL: {}", imageUrl);
        }

        // Save the post to the database
        post = repository.save(post);
        log.info("Post created successfully with ID: {}", post.getId());

        // Map and return the response DTO
        return mapper.map(post, PostCreateResponseDto.class);
    }

    /**
     * Retrieves a list of all posts, optionally filtered by username, sorted by creation date (descending).
     *
     * @param username the username to filter posts by (optional)
     * @return a list of PostDto objects
     */
    public List<PostDto> list(String username) {
        log.debug("Retrieving posts for username: {}", username);

        List<Post> posts = username != null ? repository.findByUsername(username) : repository.findAll();

        // Sort posts by creation date (descending) and map them to PostDto
        List<PostDto> postDtos = posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> mapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        log.info("Retrieved {} posts for username: {}", postDtos.size(), username);
        return postDtos;
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param id the ID of the post
     * @return the PostDto object for the given ID
     */
    public PostDto getPostById(String id) {
        log.debug("Retrieving post with ID: {}", id);

        return repository.findById(id)
                .map(post -> mapper.map(post, PostDto.class))
                .orElseThrow(() -> {
                    log.error("Post with ID {} not found", id);
                    return new RuntimeException("Post not found");
                });
    }

    /**
     * Retrieves the image URL associated with a post by its ID.
     *
     * @param id the ID of the post
     * @return a PostFileUrlDto containing the image URL
     */
    public PostFileUrlDto getImageUrlByPostId(String id) {
        log.debug("Retrieving image URL for post with ID: {}", id);

        Post post = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Post with ID {} not found", id);
                    return new RuntimeException("Post not found with ID: " + id);
                });

        String imageUrl = post.getImageUrl();

        if (imageUrl == null || imageUrl.isEmpty()) {
            log.error("Post with ID {} does not have an associated image", id);
            throw new IllegalArgumentException("Post with ID " + id + " does not have an associated image.");
        }

        log.info("Retrieved image URL for post with ID {}: {}", id, imageUrl);
        return new PostFileUrlDto(imageUrl);
    }
}
