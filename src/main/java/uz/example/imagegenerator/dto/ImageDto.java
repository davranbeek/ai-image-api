package uz.example.imagegenerator.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class ImageDto {

    // ─── Request ──────────────────────────────────
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "Prompt bo'sh bo'lmasligi kerak")
        @Size(min = 3, max = 1000, message = "Prompt 3 dan 1000 gacha belgi bo'lishi kerak")
        private String prompt;

        @Min(value = 1, message = "Minimum 1 ta rasm")
        @Max(value = 4, message = "Maksimum 4 ta rasm")
        @Builder.Default
        private Integer sampleCount = 1;

        @Pattern(
            regexp = "^(1:1|16:9|9:16|3:4|4:3)$",
            message = "AspectRatio faqat: 1:1, 16:9, 9:16, 3:4, 4:3 bo'lishi mumkin"
        )
        @Builder.Default
        private String aspectRatio = "1:1";
    }

    // ─── Response ─────────────────────────────────
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String prompt;
        private String aspectRatio;
        private Integer sampleCount;
        private String imageData;
        private String status;
        private String errorMessage;
        private String createdAt;
    }

    // ─── Gemini API ichki DTO ─────────────────────
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GeminiRequest {
        private java.util.List<Instance> instances;
        private Parameters parameters;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class Instance {
            private String prompt;
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class Parameters {
            private Integer sampleCount;
            private String aspectRatio;
            private String personGeneration;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiResponse {
        private java.util.List<Prediction> predictions;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class Prediction {
            private String bytesBase64Encoded;
            private String mimeType;
        }
    }
}
