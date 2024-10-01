package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.*;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.*;
import com.spot.good2travel.dto.FolderRequest;
import com.spot.good2travel.dto.FolderResponse;
import com.spot.good2travel.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FolderService {

    private final FolderRepository folderRepository;
    private final ItemRepository itemRepository;
    private final ItemFolderRepository itemFolderRepository;
    private final UserRepository userRepository;
    private final LocalGovernmentRepository localGovernmentRepository;
    private final ItemService itemService;
    private final ImageService imageService;

    /*
    새 폴더 만들기
     */
    public Long create(FolderRequest.FolderCreateRequest folderRequest, UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.USER_NOT_FOUND));
        //전부 of를 썼기때문에 일관성으로 여기도 of를 추가합니다.
        Folder newFolder = Folder.of(folderRequest, user);
        folderRepository.save(newFolder);
        log.info("[createFolder] 새 폴더 생성");
        return newFolder.getId();
    }

    /*
    폴더 수정
     */
    @Transactional
    public Long updateFolder(FolderRequest.FolderUpdateRequest request, Long folderId, UserDetails userDetails) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FOLDER_NOT_FOUND));
        validIsOwner(folder.getUser(), userDetails);
        List<Long> sequence = folder.getSequence();
        String imageName = null;

        if (sequence != null && !sequence.isEmpty()){
            imageName = findListImage(folder.getItemFolders());
        }

        folder.updateFolder(request, sequence, imageName);

        return folderId;
    }

    /*
    GOODE가 없는 경우도 있어서 상의 결과 첫 이모지 혹은 사진 반환으로 결정
     */
    @Transactional
    public String findListImage(List<ItemFolder> itemFolders) {
        return itemFolders.stream()
                .map(ItemFolder::getItem)
                .filter(item -> item.getType().equals(ItemType.GOODE))
                .map(Item::getImageUrl)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(imageService.getDefaultImageUrl());
    }

    @Transactional
    public Long createItemFolder(Long folderId, FolderRequest.ItemFolderCreateRequest request, UserDetails userDetails) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FOLDER_NOT_FOUND));
        Set<Long> sequence = new HashSet<>(folder.getSequence());

        if(sequence.contains(request.getItemId())){
            throw new ItemIsExistException(ExceptionMessage.ITEM_IS_EXIST);
        }

        Long itemFolderId = createItemFolder(folder, request, userDetails);

        folder.updateImageName(findListImage(folder.getItemFolders()));

        return itemFolderId;
    }

    @Transactional
    public Long createItemFolder(Folder folder, FolderRequest.ItemFolderCreateRequest request, UserDetails userDetails) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));

        validIsOwner(folder.getUser(), userDetails);
        String emoji = request.getEmoji();
        ItemFolder itemFolder = ItemFolder.of(emoji, item, folder);

        itemFolderRepository.save(itemFolder);

        List<Long> newSequence = folder.getSequence();
        newSequence.add(itemFolder.getId());
        folder.updateFolderSequence(newSequence);
        folderRepository.save(folder);

        return folder.getId();
    }

    @Transactional
    public List<Long> createItemFolders(Long folderId, List<FolderRequest.ItemFolderCreateRequest> requests, UserDetails userDetails){
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FOLDER_NOT_FOUND));
        Set<Long> sequence = new HashSet<>(folder.getSequence());

        List<Long> itemFoldersIds = requests.stream()
                .filter(itemFolder -> !sequence.contains(itemFolder.getItemId()))
                .map(itemFolder -> createItemFolder(folder, itemFolder, userDetails)).toList();

        folder.updateImageName(findListImage(folder.getItemFolders()));

        return itemFoldersIds;
    }

    /*
    폴더 목록 조회
     */
    @Transactional
    public List<FolderResponse.FolderListResponse> getFolderList(UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        List<Folder> folders = folderRepository.findAllByUserId(userId);
        return folders
                .stream()
                .map(this::toListResponse)
                .toList();
    }

    @Transactional
    public FolderResponse.FolderListResponse toListResponse(Folder folder){
        log.info("[getFolderList] 폴더 목록 조회");
        return new FolderResponse.FolderListResponse(folder.getId(), folder.getTitle(), folder.getImageName());
    }

    /*
    폴더 안 계획 조회
     */
    @Transactional
    public FolderResponse.FolderDetailResponse getFolderDetail(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FOLDER_NOT_FOUND));

        return FolderResponse.FolderDetailResponse.of(folder, getItemFolders(folder));
    }

    @Transactional
    public List<FolderResponse.ItemFolderResponse> getItemFolders(Folder folder) {
        return folder.getSequence().stream()
                .map(num -> {
                    ItemFolder itemFolder = itemFolderRepository.findById(num)
                            .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.ITEM_FOLDER_NOT_FOUND));
                    Item item = itemFolder.getItem();
                    if(item.getType().equals(ItemType.GOODE)){
                        String imageUrl = item.getImageUrl() != null ? item.getImageUrl() : imageService.getDefaultImageUrl();
                        return FolderResponse.ItemFolderResponse
                                .of(item, itemFolder, itemFolder.getIsFinished(), imageUrl);
                    }
                    return FolderResponse.ItemFolderResponse
                                .of(itemFolder.getItem(), itemFolder, itemFolder.getIsFinished(), itemFolder.getEmoji());
                }).toList();
    }

    @Transactional
    public Long deleteItemFolder(Long folderId, Long itemFolderId, UserDetails userDetails) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(()->new NotFoundElementException(ExceptionMessage.FOLDER_NOT_FOUND));
        validIsOwner(folder.getUser(), userDetails);

        ItemFolder itemFolder = itemFolderRepository.findById(itemFolderId)
                .orElseThrow(()->new NotFoundElementException(ExceptionMessage.ITEM_FOLDER_NOT_FOUND));

        Item item = itemFolder.getItem();

        itemService.deleteUserItem(item);

        folder.getSequence().removeIf(num -> num.equals(itemFolder.getId()));

        itemFolderRepository.delete(itemFolder);
        folderRepository.save(folder);

        return folderId;
    }

    /*
    폴더 삭제
     */
    @Transactional
    public void deleteFolder(Long folderId, UserDetails userDetails) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.FOLDER_NOT_FOUND));
        validIsOwner(folder.getUser(), userDetails);
        //공식적이지 않은 item들만 삭제

        folder.getItemFolders()
                .forEach(item -> itemService.deleteUserItem(item.getItem()));

        folderRepository.deleteById(folderId);
        log.info("[deleteFolder] {}번 폴더 삭제",folderId);
    }

    public void validIsOwner(User user, UserDetails userDetails){
        if(userDetails == null){
            throw new JwtEmptyException(ExceptionMessage.TOKEN_NOT_FOUND);
        }
        Long userId = ((CustomUserDetails) userDetails).getId();
        if(!user.getId().equals(userId)){
            throw new NotAuthorizedUserException(ExceptionMessage.USER_UNAUTHENTICATED);
        }
    }

    @Transactional
    public Boolean switchIsFinished(Long itemFolderId){
        ItemFolder itemFolder = itemFolderRepository.findById(itemFolderId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.ITEM_FOLDER_NOT_FOUND));
        itemFolder.switchIsFinished();

        return itemFolder.getIsFinished();
    }

    @Transactional
    public Long updateItemFolder(FolderRequest.ItemFolderUpdateRequest request, UserDetails userDetails) {
        ItemFolder itemFolder = itemFolderRepository.findById(request.getItemFolderId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_FOLDER_NOT_FOUND));

        validIsOwner(itemFolder.getFolder().getUser(), userDetails);

        if (itemFolder.getItem().getIsOfficial()){
            if(itemFolder.getItem().getType().equals(ItemType.GOODE)){
                throw new ItemAccessException(ExceptionMessage.ITEM_UPDATE_BAD_REQUEST);
            }
            else{
                itemFolder.updateEmoji(request.getEmoji());
            }
        } else {
            LocalGovernment localGovernment = localGovernmentRepository.findById(request.getLocalGovernmentId())
                    .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.LOCAL_GOVERNMENT_NOT_FOUND));
            itemFolder.updateEmoji(request.getEmoji());
            itemFolder.getItem().updateUserItem(request, localGovernment);
        }
        return itemFolder.getFolder().getId();
    }
}
