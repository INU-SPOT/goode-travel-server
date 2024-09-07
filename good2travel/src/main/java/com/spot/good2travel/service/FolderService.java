package com.spot.good2travel.service;

import com.spot.good2travel.domain.Folder;
import com.spot.good2travel.dto.FolderRequest;
import com.spot.good2travel.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    /*
    새 폴더 만들기
     */
    public void create(FolderRequest.FolderCreate folderRequest) {
        Folder newFolder = Folder.builder()
                .title(folderRequest.getTitle())
//                .user() todo 유저 추가
                .build();
        folderRepository.save(newFolder);
    }
}
