package i.storage;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface FileStorage {
    Mono<String> uploadImage(MultipartFile image);
    String getImageUrl(String imageId);
}
