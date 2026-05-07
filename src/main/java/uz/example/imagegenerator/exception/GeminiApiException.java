package uz.example.imagegenerator.exception;

public class GeminiApiException extends RuntimeException {
    private final int statusCode;

    public GeminiApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public GeminiApiException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
