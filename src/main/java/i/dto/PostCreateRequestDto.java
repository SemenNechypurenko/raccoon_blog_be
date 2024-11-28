package i.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new post.
 * Contains the title and content for the new post.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequestDto {

    /**
     * The title of the post.
     * This field is required and should not be empty or blank.
     */
    @NotBlank(message = "Title should not be empty or blank")
    private String title;

    /**
     * The content of the post.
     * This field is required and should not be empty or blank.
     */
    @NotBlank(message = "Content should not be empty or blank")
    private String content;
}
