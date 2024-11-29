package i.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class FileUploadConfig {

    @Value("${upload.path}")
    private String uploadPath;

}
