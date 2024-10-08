package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.itemPosts WHERE p.id = :postId")
    Optional<Post> findPostWithItemsById(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.user WHERE p.user.id = :userId")
    Page<Post> findPostsByUserId(@Param("userId") Long userId, Pageable pageable);

    Page<Post> findAllByIdIn(List<Long> postId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.itemPosts ip " +
            "LEFT JOIN ip.item i " +
            "LEFT JOIN i.localGovernment lg " +
            "LEFT JOIN i.itemCategories ic " +
            "LEFT JOIN ic.category c " +
            "WHERE ( " +
            "    (:categories IS NULL OR c.name IN :categories) " +
            ") " +
            "AND ( " +
            "    ( " +
            "        (:metropolitanGovernments IS NULL AND :localGovernments IS NULL) " +
            "        OR lg.metropolitanGovernment.id IN :metropolitanGovernments " +
            "        OR lg.id IN :localGovernments " +
            "    ) " +
            ") " +
            "AND ( " +
            "    :keyword IS NULL " +
            "    OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR (i IS NOT NULL AND (LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(ip.content) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            ")"
    )
    Page<Post> searchPostsByCriteria(
            @Param("metropolitanGovernments") List<Long> metropolitanGovernments,
            @Param("localGovernments") List<Long> localGovernments,
            @Param("categories") List<String> categories,
            @Param("keyword") String keyword,
            Pageable pageable);

}
