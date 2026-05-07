package uz.example.imagegenerator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.example.imagegenerator.entity.ImageEntity;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    // Status bo'yicha filtrlash
    List<ImageEntity> findByStatus(ImageEntity.ImageStatus status);

    // Prompt bo'yicha qidirish (case-insensitive)
    @Query("SELECT i FROM ImageEntity i WHERE LOWER(i.prompt) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ImageEntity> searchByPrompt(@Param("keyword") String keyword, Pageable pageable);

    // So'nggi N ta muvaffaqiyatli rasm
    List<ImageEntity> findTop10ByStatusOrderByCreatedAtDesc(ImageEntity.ImageStatus status);

    // Statistika: har bir status uchun soni
    @Query("SELECT i.status, COUNT(i) FROM ImageEntity i GROUP BY i.status")
    List<Object[]> countByStatus();
}
