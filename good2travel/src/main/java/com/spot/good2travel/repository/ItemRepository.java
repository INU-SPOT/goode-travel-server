package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.ItemType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.type=0 ORDER BY RAND() LIMIT 1")
    Optional<Item> findRandomItem();

    @Query("SELECT DISTINCT i FROM Item i " +
            "LEFT JOIN i.localGovernment lg " +
            "LEFT JOIN i.itemCategories ic " +
            "LEFT JOIN ic.category c " +
            "WHERE i.type = :type AND i.isOfficial = true " +
            "AND (:categories IS NULL OR c.name IN :categories) " +
            "AND ((:metropolitanGovernments IS NULL AND :localGovernments IS NULL) " +
            "     OR lg.metropolitanGovernment.id IN :metropolitanGovernments " +
            "     OR lg.id IN :localGovernments) " +
            "AND (:keyword IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')))"
    )
    Page<Item> searchGoode(
            @Param("metropolitanGovernments") List<Long> metropolitanGovernments,
            @Param("localGovernments") List<Long> localGovernments,
            @Param("categories") List<String> categories,
            @Param("keyword") String keyword,
            @Param("type") ItemType type,
            Pageable pageable);

    Page<Item> findAllByType(ItemType type, Pageable pageable);

    List<Item> findAllByTypeAndIsOfficialIsTrueAndImageUrlIsNotNull(ItemType type);
}
