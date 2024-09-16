package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUserId(long userId);
}
