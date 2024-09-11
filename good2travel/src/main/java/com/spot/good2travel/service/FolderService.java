package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.FolderException;
import com.spot.good2travel.common.exception.ItemException;
import com.spot.good2travel.domain.Folder;
import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.ItemFolder;
import com.spot.good2travel.dto.FolderRequest;
import com.spot.good2travel.dto.FolderResponse;
import com.spot.good2travel.dto.ItemListResponse;
import com.spot.good2travel.dto.record.Goode;
import com.spot.good2travel.dto.record.Plan;
import com.spot.good2travel.repository.FolderRepository;
import com.spot.good2travel.repository.ItemFolderRepository;
import com.spot.good2travel.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public void create(FolderRequest.FolderCreateRequest folderRequest) {
        Folder newFolder = Folder.builder()
                .title(folderRequest.getTitle())
//                .user() todo 유저 추가
                .build();
        folderRepository.save(newFolder);
        log.info("[createFolder] 새 폴더 생성");
    }

    /*
    계획 순서 수정
     */
    @Transactional
    public Object updatePlanList(FolderRequest.PlanListUpdateRequest planUpdateRequest, Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderException(ExceptionMessage.FOLDER_NOT_FOUND));
        folder.updateSequence(planUpdateRequest.getSequence());
        return folder.getSequence();
    }

    /*
    폴더 제목 수정
     */
    @Transactional
    public Object updateFolderTitle(FolderRequest.FolderTitleUpdateRequest folderUpdateRequest, Long folderId){
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderException(ExceptionMessage.FOLDER_NOT_FOUND));
        folder.updateTitle(folderUpdateRequest.getTitle());
        return folder.getTitle();
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
        List<ItemFolder> itemFolders = folder.getItemFolders();
        Optional<Item> goodeItem = itemFolders.stream()
                .map(ItemFolder::getItem)
                .filter(item -> item.getType() == Item.ItemType.GOODE)
                .min(Comparator.comparing(Item::getCreateDate));
        log.info("[getFolderList] 폴더 목록 조회");
        return goodeItem
                .map(item -> new FolderResponse.FolderListResponse(folder.getTitle(), item.getImageUrl()))
                .orElseGet(() -> new FolderResponse.FolderListResponse(folder.getTitle(), null));
    }

    /*
    폴더 안 계획 조회
     */
    @Transactional
    public ItemListResponse getItemList(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderException(ExceptionMessage.FOLDER_NOT_FOUND));
        return getGoodeAndPlan(folder);
    }

    @NotNull
    private ItemListResponse getGoodeAndPlan(Folder folder) {
        List<Integer> itemSequence = folder.getSequence();
        List<Goode> goodeList = new ArrayList<>();
        List<Plan> planList = new ArrayList<>();
        for (Integer id : itemSequence){
            Item item = itemRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new ItemException(ExceptionMessage.ITEM_NOT_FOUND));
            ItemFolder itemFolder = itemFolderRepository.findByItemId(Long.valueOf(id));
            if (item.getType() == Item.ItemType.GOODE){
                Goode goode = new Goode(
                        item.getId(),
                        item.getTitle(),
                        item.getImageUrl()==null ? item.getEmoji() : item.getImageUrl(),
                        item.getAddress(),
                        itemFolder.getIsMain()
                );
                goodeList.add(goode);
            } else {
                Plan plan = new Plan(
                        item.getId(),
                        item.getTitle(),
                        item.getImageUrl()==null ? item.getEmoji() : item.getImageUrl(),
                        item.getCreateDate().toLocalDate(),
                        itemFolder.getIsFinished()
                );
                planList.add(plan);
            }
        }
        log.info("[getItemList] {}번 폴더의 계획 목록 조회", folder.getId());
        return new ItemListResponse(goodeList, planList);
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
