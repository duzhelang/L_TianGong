package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.EmissionFactor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmissionFactorRepository extends JpaRepository<EmissionFactor, Long> {
    Optional<EmissionFactor> findByFactorCode(String factorCode);

    List<EmissionFactor> findByCategory(String category);

    List<EmissionFactor> findByGasType(String gasType);

    List<EmissionFactor> findByStatus(String status);

    List<EmissionFactor> findByCategoryAndStatus(String category, String status);

    List<EmissionFactor> findByCategoryAndSubcategory(String category, String subcategory);

    List<EmissionFactor> findBySource(String source);

    List<EmissionFactor> findByRegion(String region);

    @Query("SELECT ef FROM EmissionFactor ef WHERE ef.category = :category AND ef.gasType = :gasType AND ef.status = 'ACTIVE'")
    List<EmissionFactor> findActiveByCategoryAndGasType(@Param("category") String category, @Param("gasType") String gasType);

    @Query("SELECT ef FROM EmissionFactor ef WHERE ef.factorCode LIKE %:keyword% OR ef.factorName LIKE %:keyword%")
    List<EmissionFactor> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT ef.category FROM EmissionFactor ef WHERE ef.status = 'ACTIVE'")
    List<String> findAllCategories();

    @Query("SELECT DISTINCT ef.subcategory FROM EmissionFactor ef WHERE ef.category = :category AND ef.status = 'ACTIVE'")
    List<String> findSubcategoriesByCategory(@Param("category") String category);
}