package com.spot.good2travel.repository;

import com.spot.good2travel.domain.ItemPost;
import com.spot.good2travel.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPostRepository extends JpaRepository<ItemPost, Long> {

    List<ItemPost> findItemPostsByPost(Post post);

}
