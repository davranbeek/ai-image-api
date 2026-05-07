package uz.example.imagegenerator.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import uz.example.imagegenerator.dto.ImageDto;
import uz.example.imagegenerator.entity.ImageEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-07T16:23:59+0500",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.jar, environment: Java 23.0.2 (Oracle Corporation)"
)
@Component
public class ImageMapperImpl implements ImageMapper {

    @Override
    public ImageEntity toEntity(ImageDto.Request request) {
        if ( request == null ) {
            return null;
        }

        ImageEntity.ImageEntityBuilder imageEntity = ImageEntity.builder();

        imageEntity.prompt( request.getPrompt() );
        imageEntity.aspectRatio( request.getAspectRatio() );
        imageEntity.sampleCount( request.getSampleCount() );

        imageEntity.status( ImageEntity.ImageStatus.PENDING );

        return imageEntity.build();
    }

    @Override
    public ImageDto.Response toResponse(ImageEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ImageDto.Response.ResponseBuilder response = ImageDto.Response.builder();

        response.id( entity.getId() );
        response.prompt( entity.getPrompt() );
        response.aspectRatio( entity.getAspectRatio() );
        response.sampleCount( entity.getSampleCount() );
        response.imageData( entity.getImageData() );
        response.errorMessage( entity.getErrorMessage() );

        response.status( entity.getStatus().name() );
        response.createdAt( entity.getCreatedAt() != null ? entity.getCreatedAt().format(FORMATTER) : null );

        return response.build();
    }

    @Override
    public void updateEntity(ImageDto.Request request, ImageEntity entity) {
        if ( request == null ) {
            return;
        }

        if ( request.getPrompt() != null ) {
            entity.setPrompt( request.getPrompt() );
        }
        if ( request.getAspectRatio() != null ) {
            entity.setAspectRatio( request.getAspectRatio() );
        }
        if ( request.getSampleCount() != null ) {
            entity.setSampleCount( request.getSampleCount() );
        }
    }
}
