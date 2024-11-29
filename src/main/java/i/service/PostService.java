package i.service;

import i.dto.PostCreateResponseDto;
import i.dto.PostDto;
import i.model.Post;
import i.repository.PostRepository;
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
    private final ImgurService imgurService;

    public PostCreateResponseDto createPost(String title, String content, MultipartFile image, String username) {
        // Создаем новый объект Post
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUsername(username);

        // Если изображение передано, сохраняем его
        if (image != null) {
            String imageUrl = imgurService.uploadImage(image).block();
            post.setImageUrl(imageUrl);  // Устанавливаем URL изображения в пост
        }

        // Сохраняем пост в базу данных
        post = repository.save(post);

        // Возвращаем ответ в формате DTO
        return mapper.map(post, PostCreateResponseDto.class);
    }

//    private String saveImage(MultipartFile image) {
//        try {
//            // Генерируем уникальное имя для файла и сохраняем его
//            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
//            Path filePath = Paths.get(fileUploadConfig.getUploadPath(), filename);
//            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//            return filename; // Или полный путь, если необходимо
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to store image", e);
//        }
//    }


    /**
     * Retrieves a list of all posts, sorted by creation date in descending order.
     *
     * @return a list of PostDto objects representing all posts
     */
    public List<PostDto> list(String username) {
        List<Post> posts = username != null ? repository.findByUsername(username) : repository.findAll();

        // Sort posts by creation date (descending), map them to PostDto, and collect as a list
        return posts.stream().sorted(Comparator.comparing(Post::getCreatedAt).reversed()).map(post -> mapper.map(post, PostDto.class)).collect(Collectors.toList());
    }

    public PostDto getPostById(String id) {
        return repository.findById(id).map(post -> mapper.map(post, PostDto.class)).orElseThrow(() -> new RuntimeException("Post not found"));
    }

}
