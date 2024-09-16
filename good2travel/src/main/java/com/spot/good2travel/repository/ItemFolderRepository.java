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

    @Query("SELECT i FROM ItemFolder if " +
            "JOIN Item i ON i.id = if.item.id " +
            "JOIN Folder f ON if.folder.id = f.id " +
            "WHERE i.isOfficial = false AND f.id = :folderId")
    List<Item> findUnOfficialItemsInFolder(Long folderId);
}
