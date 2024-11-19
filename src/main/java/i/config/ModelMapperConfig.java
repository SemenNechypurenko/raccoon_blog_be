package i.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import i.dto.CommentCreateRequestDto;
import i.model.Comment;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true) // Позволяет использовать поля напрямую
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Настройка для маппинга CommentCreateRequestDto -> Comment
        modelMapper.addMappings(new PropertyMap<CommentCreateRequestDto, Comment>() {
            @Override
            protected void configure() {
                skip(destination.getId()); // Пропустить поле id
            }
        });

        return modelMapper;
    }
}
