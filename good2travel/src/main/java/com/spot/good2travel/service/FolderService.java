package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.domain.Folder;
import com.spot.good2travel.domain.Item;
import com.spot.good2travel.dto.FolderRequest;
import com.spot.good2travel.dto.FolderResponse;
import com.spot.good2travel.repository.FolderRepository;
import com.spot.good2travel.repository.ItemFolderRepository;
import com.spot.good2travel.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FolderService {

    private final FolderRepository folderRepository;
    private final ItemRepository itemRepository;
    private final ItemFolderRepository itemFolderRepository;

    /*
    새 폴더 만들기
     */
    public void create(FolderRequest.FolderCreateRequest folderRequest) {
        Folder newFolder = Folder.builder()
                .title(folderRequest.getTitle())
//                .user() todo 유저 추가
                .build();
        folderRepository.save(newFolder);
        log.info("[createFolder] 새 폴더 생성");
    }

    /*
    폴더 수정
     */
    @Transactional
    public FolderResponse.FolderUpdateResponse updatePlanList(FolderRequest.FolderUpdateRequest folderUpdateRequest, Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FOLDER_NOT_FOUND));
        folder.updateSequence(folderUpdateRequest.getSequence());
        folder.updateTitle(folderUpdateRequest.getTitle());
        return new FolderResponse.FolderUpdateResponse(
                folder.getTitle(), folder.getSequence()
        );
    }

    /*
    폴더 목록 조회
     */
    @Transactional
    public List<FolderResponse.FolderListResponse> getFolderList() {
        long userId = 1; //todo 유저 정보
        List<Folder> folders = folderRepository.findAllByUserId(userId);
        return folders
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    public FolderResponse.FolderListResponse toListResponse(Folder folder){
        Item item = itemRepository.findById(folder.getMainGoode().getId())
                        .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));
        log.info("[getFolderList] 폴더 목록 조회");
        return new FolderResponse.FolderListResponse(folder.getTitle(), item.getImageUrl());
    }

    /*
    폴더 안 계획 조회
     */
    @Transactional
    public FolderResponse.ItemListResponse getItemList(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FOLDER_NOT_FOUND));
        return getItems(folder);
    }

    public FolderResponse.ItemListResponse getItems(Folder folder) {
        List<Integer> sequence = folder.getSequence();
        List<FolderResponse.FolderItem> folderItems =
                sequence.stream()
                .map(s -> itemRepository.findById(Long.valueOf(s)).orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND)))
                .map(item -> new FolderResponse.FolderItem().of(item))
                .toList();
        return new FolderResponse.ItemListResponse(folderItems);
    }


    /*
    폴더 삭제
     */
    @Transactional
    public void deleteFolder(Long folderId) {
        //공식적이지 않은 item들만 삭제
        List<Item> items = itemFolderRepository.findUnOfficialItemsInFolder(folderId);
        itemRepository.deleteAll(items);
        folderRepository.deleteById(folderId);
        log.info("[deleteFolder] {}번 폴더 삭제",folderId);
    }
}
