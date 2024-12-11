package i.fileStorageClient;

import com.fasterxml.jackson.databind.JsonNode;
import i.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Service class for handling image operations with the Imgur API.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImgurService implements FileStorage {

    private final WebClient.Builder webClientBuilder;
    private final PostRepository postRepository;

    @Value("${imgur.api.base-url}")
    private String baseUrl;  // Base URL for the Imgur API

    @Value("${imgur.client-id}")
    private String clientId;  // Client ID for Imgur API authorization

    private WebClient webClient;

    /**
     * Uploads an image to Imgur and retrieves the image URL.
     *
     * @param image the image to upload
     * @return a Mono emitting the URL of the uploaded image
     */
    @Override
    public Mono<String> uploadImage(MultipartFile image) {
        log.info("Starting image upload process.");

        // Ensure the image is not empty
        if (image.isEmpty()) {
            log.error("The image is empty, cannot upload.");
            return Mono.error(new IllegalArgumentException("Image must not be empty"));
        }

        // Prepare form data for the request
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("image", image.getResource());

        // Initialize WebClient with the Imgur base URL
        webClient = webClientBuilder.baseUrl(baseUrl).build();

        // Send POST request to Imgur API for image upload
        log.info("Sending image upload request to Imgur API.");
        return webClient.post()
                .uri("/3/image")  // Endpoint for image upload
                .header("Authorization", "Client-ID " + clientId)  // Add Authorization header
                .contentType(MediaType.MULTIPART_FORM_DATA)  // Set content type to multipart/form-data
                .bodyValue(formData)  // Add form data to the request body
                .retrieve()  // Execute the request and retrieve the response
                .bodyToMono(JsonNode.class)  // Parse response as JsonNode
                .map(response -> {
                    String imageUrl = response.path("data").path("link").asText();
                    log.info("Image uploaded successfully, URL: {}", imageUrl);
                    return imageUrl;  // Extract image URL from the response
                });
    }

    @Override
    public String getImageUrl(String postId) {
        log.info("Retrieving image URL for post with ID: {}", postId);

        return postRepository.findById(postId)
                .map(post -> {
                    String imageUrl = post.getImageUrl();
                    if (imageUrl == null || imageUrl.isEmpty()) {
                        log.error("Post with ID {} does not have an image.", postId);
                        throw new IllegalArgumentException("Post with ID " + postId + " does not have an image.");
                    }
                    log.info("Image URL retrieved successfully: {}", imageUrl);
                    return imageUrl;
                })
                .orElseThrow(() -> {
                    log.error("Post not found with ID: {}", postId);
                    return new IllegalArgumentException("Post not found with ID: " + postId);
                });
    }

    /**
     * Retrieves an image as a byte array using its direct URL.
     *
     * @param url the direct URL of the image
     * @return the image as a byte array
     */
    public byte[] getImageByDirectUrl(String url) {
        log.info("Retrieving image from URL: {}", url);

        // Initialize WebClient without a base URL to allow direct URL usage
        webClient = webClientBuilder.build();
        try {
            // Execute GET request to fetch the image
            return webClient.get()
                    .uri(url)  // Use the full URL as the request URI
                    .retrieve()  // Retrieve the response
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> {
                                log.error("Error occurred while retrieving the image: HTTP status {}", clientResponse.statusCode());
                                return Mono.error(new RuntimeException("Image not found or server error occurred"));
                            })
                    .bodyToMono(byte[].class)  // Parse response body as a byte array
                    .block();  // Block to wait for the response
        } catch (Exception e) {
            log.error("Failed to retrieve image from URL: {}", url, e);
            throw new RuntimeException("Failed to retrieve image from URL: " + url, e);
        }
    }
}
