package uz.example.imagegenerator.mapper;

import org.mapstruct.*;
import uz.example.imagegenerator.dto.ImageDto;
import uz.example.imagegenerator.entity.ImageEntity;

import java.time.format.DateTimeFormatter;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ImageMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Request DTO → Entity
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "imageData",    ignore = true)
    @Mapping(target = "filePath",     ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "createdAt",    ignore = true)
    @Mapping(target = "updatedAt",    ignore = true)
    @Mapping(target = "status",       constant = "PENDING")
    ImageEntity toEntity(ImageDto.Request request);

    // Entity → Response DTO
    @Mapping(target = "status",    expression = "java(entity.getStatus().name())")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().format(FORMATTER) : null)")
    ImageDto.Response toResponse(ImageEntity entity);

    // Entity ni yangilash (PUT/PATCH uchun)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status",    ignore = true)
    void updateEntity(ImageDto.Request request, @MappingTarget ImageEntity entity);
}
