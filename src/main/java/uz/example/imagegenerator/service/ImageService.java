package uz.example.imagegenerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.example.imagegenerator.dto.ImageDto;

import java.util.List;

public interface ImageService {

    /**
     * Gemini orqali yangi rasm generatsiya qiladi va DB ga saqlaydi
     */
    ImageDto.Response generateAndSave(ImageDto.Request request);

    /**
     * ID orqali rasm ma'lumotlarini qaytaradi
     */
    ImageDto.Response getById(Long id);

    /**
     * Barcha rasmlar (sahifalash bilan)
     */
    Page<ImageDto.Response> getAll(Pageable pageable);

    /**
     * Prompt kalit so'zi bo'yicha qidirish
     */
    Page<ImageDto.Response> searchByPrompt(String keyword, Pageable pageable);

    /**
     * So'nggi muvaffaqiyatli 10 ta rasm
     */
    List<ImageDto.Response> getRecentSuccessful();

    /**
     * Rasm yozuvini o'chirish
     */
    void delete(Long id);

  byte[] getImageBytes(Long id);
}
