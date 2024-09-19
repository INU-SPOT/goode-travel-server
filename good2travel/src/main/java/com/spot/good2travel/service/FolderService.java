package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.*;
import com.spot.good2travel.dto.FolderRequest;
import com.spot.good2travel.dto.FolderResponse;
import com.spot.good2travel.repository.FolderRepository;
import com.spot.good2travel.repository.ItemFolderRepository;
import com.spot.good2travel.repository.ItemRepository;
import com.spot.good2travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FolderService {

    private final FolderRepository folderRepository;
    private final ItemRepository itemRepository;
    private final ItemFolderRepository itemFolderRepository;
    private final UserRepository userRepository;

    /*
    새 폴더 만들기
     */
    public String create(FolderRequest.FolderCreateRequest folderRequest, UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        Folder newFolder = Folder.builder()
                .title(folderRequest.getTitle())
                .user(user)
                .build();
        folderRepository.save(newFolder);
        log.info("[createFolder] 새 폴더 생성");
        return newFolder.getTitle();
    }

    /*
    폴더 수정 및 계획 생성
     */
    @Transactional
    public FolderResponse.FolderUpdateResponse updatePlanList(FolderRequest.FolderUpdateRequest folderUpdateRequest, Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FOLDER_NOT_FOUND));

        List<Item> items = createItemFolder(folderUpdateRequest, folderId, folder);
        findMainGoode(items, folder); //메인 굳이 찾기
        return new FolderResponse.FolderUpdateResponse(folder.getTitle(), folder.getSequence());
    }

    private static void findMainGoode(List<Item> items, Folder folder) {
        Optional<Item> goode = items.stream()
                .filter(item -> item.getType()== ItemType.GOODE)
                .findFirst();
        goode.ifPresent(folder::updateMainGoode);
    }

    private List<Item> createItemFolder(FolderRequest.FolderUpdateRequest folderUpdateRequest, Long folderId, Folder folder) {
        List<Item> items = folderUpdateRequest.getSequence().stream()
                .map(itemId -> itemRepository.findById(Long.valueOf(itemId))
                        .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND)))
                .toList();

        List<ItemFolder> itemFolders = items.stream()
                .map(item -> itemFolderRepository.findByItemIdAndFolderId(item.getId(), folderId)
                        .orElseGet(() -> ItemFolder.of(item, folder)))
                .toList();

        itemFolderRepository.saveAll(itemFolders);
        folder.updateFolder(folderUpdateRequest);
        return items;
    }

    /*
    폴더 목록 조회
     */
    @Transactional
    public List<FolderResponse.FolderListResponse> getFolderList(UserDetails userDetails) {
        long userId = ((CustomUserDetails) userDetails).getId();
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
    public List<FolderResponse.ItemResponse> getItemList(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FOLDER_NOT_FOUND));
        return getItems(folder);
    }

    public List<FolderResponse.ItemResponse> getItems(Folder folder) {
        List<ItemFolder> itemFolders = folder.getItemFolders();
        return itemFolders.stream()
                .map(itemFolder -> FolderResponse.ItemResponse.of(itemFolder.getItem(), itemFolder.getIsFinished()))
                .toList();
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
