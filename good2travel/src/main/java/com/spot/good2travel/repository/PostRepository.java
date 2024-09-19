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

//    @Query("SELECT p FROM Post p " +
//            "JOIN p.itemPosts ip " +
//            "JOIN ip.item i " +
//            "JOIN i.itemCategories ic " +
//            "JOIN ic.category c " +
//            "WHERE (i.localGovernment.name IN :localGovernments " +
//            "OR c.name IN :categories " +
//            "OR i.title LIKE %:keyword%) ")
//    Page<Post> searchPostsByCriteria(@Param("localGovernments") List<String> localGovernments,
//                                     @Param("categories") List<String> categories,
//                                     @Param("keyword") String keyword,
//                                     Pageable pageable);

}
