package i.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.MediaType;

@Service
@RequiredArgsConstructor
public class ImgurService {

    private final WebClient.Builder webClientBuilder;

    @Value("${imgur.api.base-url}")
    private String baseUrl;  // Base URL for Imgur API

    @Value("${imgur.client-id}")
    private String clientId;

    /**
     * Uploads an image to Imgur.
     * @param image the image to upload
     * @return the URL of the uploaded image
     */
    public Mono<String> uploadImage(MultipartFile image) {
        // Ensure the image is not empty
        if (image.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Image must not be empty"));
        }

        // Create a MultiValueMap to represent the form data
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("image", image.getResource());

        // Send the image upload request to Imgur API
        WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();

        return webClient.post()  // Use `post()` on WebClient instance
                .uri("/3/image")  // endpoint for image upload
                .header("Authorization", "Client-ID " + clientId)
                .contentType(MediaType.MULTIPART_FORM_DATA)  // Specify multipart form data
                .bodyValue(formData)  // Send the form data as the request body
                .retrieve()  // Send the request
                .bodyToMono(JsonNode.class)  // Deserialize the response to JsonNode
                .map(response -> response.path("data").path("link").asText());  // Extract the image URL from the response
    }
}
