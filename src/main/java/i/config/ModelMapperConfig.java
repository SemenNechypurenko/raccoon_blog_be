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
                .setFieldMatchingEnabled(true) // Allows direct use of fields
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Configuration for mapping CommentCreateRequestDto -> Comment
        modelMapper.addMappings(new PropertyMap<CommentCreateRequestDto, Comment>() {
            @Override
            protected void configure() {
                skip(destination.getId()); // Skip the id field
            }
        });

        return modelMapper;
    }
}