package i.service;

import i.dto.PostCreateRequestDto;
import i.dto.PostCreateResponseDto;
import i.model.Post;
import i.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureModelMapper() {
        // Setting to ignore the id field when mapping
        modelMapper.typeMap(PostCreateRequestDto.class, Post.class)
                .addMappings(mapper -> mapper.skip(Post::setId));
    }

    public PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto) {
        // Convert the request from DTO to entity
        Post post = convertToEntity(postCreateRequestDto);

        // Save the post to the database
        post = postRepository.save(post);

        // Convert the saved post back to a DTO for the reply
        return convertFromEntity(post);
    }

    // Convert PostCreateRequestDto to Post
    private Post convertToEntity(PostCreateRequestDto postCreateRequestDto) {
        return modelMapper.map(postCreateRequestDto, Post.class);
    }

    // Convert Post to PostCreateResponseDto
    private PostCreateResponseDto convertFromEntity(Post post) {
        return modelMapper.map(post, PostCreateResponseDto.class);
    }
}
