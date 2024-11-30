package i.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
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
public class ImgurService {

    private final WebClient.Builder webClientBuilder;

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
    public Mono<String> uploadImage(MultipartFile image) {
        // Ensure the image is not empty
        if (image.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Image must not be empty"));
        }
        // Prepare form data for the request
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("image", image.getResource());
        // Initialize WebClient with the Imgur base URL
        webClient = webClientBuilder.baseUrl(baseUrl).build();
        // Send POST request to Imgur API for image upload
        return webClient.post()
                .uri("/3/image")  // Endpoint for image upload
                .header("Authorization", "Client-ID " + clientId)  // Add Authorization header
                .contentType(MediaType.MULTIPART_FORM_DATA)  // Set content type to multipart/form-data
                .bodyValue(formData)  // Add form data to the request body
                .retrieve()  // Execute the request and retrieve the response
                .bodyToMono(JsonNode.class)  // Parse response as JsonNode
                .map(response -> response.path("data").path("link").asText());  // Extract image URL from the response
    }

    /**
     * Retrieves an image as a byte array using its direct URL.
     *
     * @param url the direct URL of the image
     * @return the image as a byte array
     */
    public byte[] getImageByDirectUrl(String url) {
        // Initialize WebClient without a base URL to allow direct URL usage
        webClient = webClientBuilder.build();
        try {
            // Execute GET request to fetch the image
            return webClient.get()
                    .uri(url)  // Use the full URL as the request URI
                    .retrieve()  // Retrieve the response
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> Mono.error(new RuntimeException("Image not found or server error occurred")))
                    .bodyToMono(byte[].class)  // Parse response body as a byte array
                    .block();  // Block to wait for the response
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve image from URL: " + url, e);
        }
    }

}
