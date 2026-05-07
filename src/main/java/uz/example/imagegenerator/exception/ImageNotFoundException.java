package uz.example.imagegenerator.exception;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(Long id) {
        super("Rasm topilmadi. ID: " + id);
    }
}
