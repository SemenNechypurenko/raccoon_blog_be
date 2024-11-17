package i.service;

import i.dto.PostCreateRequestDto;
import i.dto.PostCreateResponseDto;
import i.dto.PostDto;
import i.model.Post;
import i.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

    // Injecting dependencies via constructor (handled by Lombok's @RequiredArgsConstructor)
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    /**
     * Creates a new post based on the provided PostCreateRequestDto.
     *
     * @param postCreateRequestDto the request DTO containing post details
     * @return a response DTO containing the created post details
     */
    public PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto,
                                            String username) {
        // Convert the request DTO to the Post entity
        Post post = modelMapper.map(postCreateRequestDto, Post.class);

        // Set the username (author) to the post
        post.setUsername(username);

        // Save the Post entity in the database
        post = postRepository.save(post);

        // Convert the saved Post entity to the response DTO and return it
        return modelMapper.map(post, PostCreateResponseDto.class);
    }

    /**
     * Retrieves a list of all posts, sorted by creation date in descending order.
     *
     * @return a list of PostDto objects representing all posts
     */
    public List<PostDto> list() {
        // Fetch all posts from the database
        List<Post> posts = postRepository.findAll();

        // Sort posts by creation date (descending), map them to PostDto, and collect as a list
        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
    }
}
