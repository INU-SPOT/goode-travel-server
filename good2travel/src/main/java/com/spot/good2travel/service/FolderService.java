package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.FolderException;
import com.spot.good2travel.common.exception.ItemException;
import com.spot.good2travel.domain.Folder;
import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.ItemFolder;
import com.spot.good2travel.dto.FolderCreateRequest;
import com.spot.good2travel.dto.FolderListResponse;
import com.spot.good2travel.dto.FolderUpdateRequest;
import com.spot.good2travel.repository.FolderRepository;
import com.spot.good2travel.repository.ItemFolderRepository;
import com.spot.good2travel.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    public void create(FolderCreateRequest folderRequest) {
        Folder newFolder = Folder.builder()
                .title(folderRequest.getTitle())
//                .user() todo 유저 추가
                .build();
        folderRepository.save(newFolder);
        log.info("[createFolder] 새 폴더 생성");
    }

    @Transactional
    public Object updatePlan(FolderUpdateRequest folderUpdateRequest, Long planId) {
        if (folderUpdateRequest.getType()==1){ //계획의 제목 수정
            Item item = itemRepository.findById(planId)
                    .orElseThrow(() -> new ItemException(ExceptionMessage.ITEM_NOT_FOUND));
            item.toUpdateTitle(folderUpdateRequest.getTitle());
            log.info("[updatePlan] 계획 제목 변경");
            return item.getTitle();
        } else if (folderUpdateRequest.getType()==2){ //계획의 순서 수정
            return updateSequence(folderUpdateRequest, planId);
        }
        throw new FolderException(ExceptionMessage.FOLDER_BAD_REQUEST);
    }

    private List<Integer> updateSequence(FolderUpdateRequest folderUpdateRequest, Long planId) {
        ItemFolder itemFolder = itemFolderRepository.findByItemId(planId);
        if (itemFolder==null) throw new ItemException(ExceptionMessage.ITEM_FOLDER_NOT_FOUND);

        List<Integer> sequence = itemFolder.getFolder().getSequence();
        sequence.remove((Object) planId.intValue());
        sequence.add(folderUpdateRequest.getPos()-1, planId.intValue());
        itemFolder.getFolder().toUpdateSequence(sequence);
        log.info("[updateSequence] 계획 순서 변경");
        return itemFolder.getFolder().getSequence();
    }

    @Transactional
    public List<FolderListResponse> getFolderList() {
        long userId = 1; //todo 유저 정보
        List<Folder> folders = folderRepository.findAllByUserId(userId);
        return folders
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    public FolderListResponse toListResponse(Folder folder){
        List<ItemFolder> itemFolders = folder.getItemFolders();
        Optional<Item> goodeItem = itemFolders.stream()
                .map(ItemFolder::getItem)
                .filter(item -> item.getType() == Item.ItemType.GOODE)
                .min(Comparator.comparing(Item::getCreateDate));

        return goodeItem
                .map(item -> new FolderListResponse(folder.getTitle(), item.getImageUrl()))
                .orElseGet(() -> new FolderListResponse(folder.getTitle(), null));
    }
}
