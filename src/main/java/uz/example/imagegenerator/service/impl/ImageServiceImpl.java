package uz.example.imagegenerator.service.impl;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.example.imagegenerator.client.HuggingFaceClient;
import uz.example.imagegenerator.dto.ImageDto;
import uz.example.imagegenerator.entity.ImageEntity;
import uz.example.imagegenerator.exception.ImageNotFoundException;
import uz.example.imagegenerator.mapper.ImageMapper;
import uz.example.imagegenerator.repository.ImageRepository;
import uz.example.imagegenerator.service.ImageService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final HuggingFaceClient huggingFaceClient;

    @Override
    @Transactional
    public ImageDto.Response generateAndSave(ImageDto.Request request) {
        log.info("Rasm generatsiya boshlandi: prompt='{}'", request.getPrompt());

        ImageEntity entity = imageMapper.toEntity(request);
        entity.setStatus(ImageEntity.ImageStatus.PENDING);
        entity = imageRepository.save(entity);

        try {
            List<String> base64Images = huggingFaceClient.generateImages(
                    request.getPrompt(),
                    request.getSampleCount(),
                    request.getAspectRatio()
            );

            entity.setImageData(base64Images.get(0));
            entity.setStatus(ImageEntity.ImageStatus.SUCCESS);
            log.info("Rasm muvaffaqiyatli yaratildi. Entity ID: {}", entity.getId());

        } catch (Exception e) {
            entity.setStatus(ImageEntity.ImageStatus.FAILED);
            entity.setErrorMessage(e.getMessage());
            log.error("Rasm yaratishda xatolik [ID: {}]: {}", entity.getId(), e.getMessage());
            throw e;
        } finally {
            imageRepository.save(entity);
        }

        return imageMapper.toResponse(entity);
    }

    @Override
    public ImageDto.Response getById(Long id) {
        ImageEntity entity = imageRepository.findById(id)
                .orElseThrow(() -> new ImageNotFoundException(id));
        return imageMapper.toResponse(entity);
    }

    @Override
    public Page<ImageDto.Response> getAll(Pageable pageable) {
        return imageRepository.findAll(pageable)
                .map(imageMapper::toResponse);
    }

    @Override
    public Page<ImageDto.Response> searchByPrompt(String keyword, Pageable pageable) {
        return imageRepository.searchByPrompt(keyword, pageable)
                .map(imageMapper::toResponse);
    }

    @Override
    public List<ImageDto.Response> getRecentSuccessful() {
        return imageRepository
                .findTop10ByStatusOrderByCreatedAtDesc(ImageEntity.ImageStatus.SUCCESS)
                .stream()
                .map(imageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!imageRepository.existsById(id)) {
            throw new ImageNotFoundException(id);
        }
        imageRepository.deleteById(id);
        log.info("Rasm o'chirildi. ID: {}", id);
    }

    @Override
    public byte[] getImageBytes(Long id) {

        ImageEntity entity = imageRepository.findById(id)
            .orElseThrow(() -> new ImageNotFoundException(id));

        return Base64.getDecoder().decode(entity.getImageData());
    }
}
