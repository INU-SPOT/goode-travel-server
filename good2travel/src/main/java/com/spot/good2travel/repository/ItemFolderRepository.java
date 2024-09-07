package com.spot.good2travel.repository;

import com.spot.good2travel.domain.ItemFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemFolderRepository extends JpaRepository<ItemFolder, Long> {
    ItemFolder findByItemId(Long itemId);
}
