package i.repository;

import i.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
    // Дополнительные методы для поиска постов можно определить здесь
}
