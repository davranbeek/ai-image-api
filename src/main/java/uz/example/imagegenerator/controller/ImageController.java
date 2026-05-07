package uz.example.imagegenerator.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.example.imagegenerator.dto.ImageDto;
import uz.example.imagegenerator.service.ImageService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/generate")
    public ResponseEntity<ImageDto.Response> generate(
        @Valid @RequestBody ImageDto.Request request) {

        log.info("POST /generate | prompt='{}'", request.getPrompt());
        ImageDto.Response response = imageService.generateAndSave(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ImageDto.Response>> getAll(
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
            ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        return ResponseEntity.ok(imageService.getAll(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ImageDto.Response>> search(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(imageService.searchByPrompt(keyword, pageable));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ImageDto.Response>> getRecent() {
        return ResponseEntity.ok(imageService.getRecentSuccessful());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        imageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/view")
    public ResponseEntity<byte[]> viewImage(@PathVariable Long id) {

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(imageService.getImageBytes(id));
    }
}
