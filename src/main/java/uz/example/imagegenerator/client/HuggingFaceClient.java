package uz.example.imagegenerator.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.example.imagegenerator.exception.GeminiApiException;

import java.net.URI;
import java.net.http.*;
import java.util.*;

@Slf4j
@Component
public class HuggingFaceClient {

  private final String apiKey;
  private final String model;
  private final HttpClient httpClient;

  public HuggingFaceClient(
      @Value("${huggingface.api.key}") String apiKey,
      @Value("${huggingface.api.model:stabilityai/stable-diffusion-xl-base-1.0}") String model) {
    this.apiKey     = apiKey;
    this.model      = model;
    this.httpClient = HttpClient.newHttpClient();
    log.info("HuggingFaceClient tayyor | model: {}", model);
  }

  public List<String> generateImages(String prompt, int sampleCount, String aspectRatio) {

    String url =
        "https://router.huggingface.co/hf-inference/models/" + model;

    log.info("URL: {} | prompt='{}'", url, prompt);

    int[] dim  = resolveDimensions(aspectRatio);
    String body = """
            {
              "inputs": "%s",
              "parameters": {
                "width": %d,
                "height": %d,
                "num_inference_steps": 30,
                "guidance_scale": 7.5,
                "negative_prompt": "blurry, low quality, ugly"
              }
            }
            """.formatted(prompt.replace("\"", "\\\""), dim[0], dim[1]);

    List<String> results = new ArrayList<>();

    for (int i = 0; i < sampleCount; i++) {
      log.info("Rasm {}/{} so'ralmoqda...", i + 1, sampleCount);
      try {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<byte[]> response = httpClient.send(
            request, HttpResponse.BodyHandlers.ofByteArray());

        log.info("HTTP status: {}", response.statusCode());

        if (response.statusCode() == 503) {
          throw new GeminiApiException("Model yuklanmoqda, 20 soniya kuting", 503);
        }
        if (response.statusCode() == 401) {
          throw new GeminiApiException("HF token noto'g'ri", 401);
        }
        if (response.statusCode() != 200) {
          throw new GeminiApiException(
              "Xato [" + response.statusCode() + "]: " + new String(response.body()),
              response.statusCode());
        }

        results.add(Base64.getEncoder().encodeToString(response.body()));
        log.info("Rasm {}/{} tayyor ({} KB)", i + 1, sampleCount, response.body().length / 1024);

      } catch (GeminiApiException e) {
        throw e;
      } catch (Exception e) {
        throw new GeminiApiException("Tarmoq xatosi: " + e.getMessage());
      }
    }
    return results;
  }

  private int[] resolveDimensions(String aspectRatio) {
    return switch (aspectRatio) {
      case "16:9" -> new int[]{1344, 768};
      case "9:16" -> new int[]{768, 1344};
      case "3:4"  -> new int[]{896, 1152};
      case "4:3"  -> new int[]{1152, 896};
      default     -> new int[]{1024, 1024};
    };
  }
}