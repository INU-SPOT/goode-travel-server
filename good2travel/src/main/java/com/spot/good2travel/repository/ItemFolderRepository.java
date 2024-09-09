package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.ItemFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemFolderRepository extends JpaRepository<ItemFolder, Long> {
    ItemFolder findByItemId(Long itemId);

    @Query("SELECT if.item FROM ItemFolder if WHERE if.item.isOfficial = false AND if.folder.id = :folderId")
    List<Item> findUnOfficialItemsInFolder(Long folderId);
}
