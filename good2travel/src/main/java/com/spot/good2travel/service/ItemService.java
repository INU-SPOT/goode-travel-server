package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.ItemAccessException;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.domain.*;
import com.spot.good2travel.dto.ItemRequest;
import com.spot.good2travel.dto.ItemResponse;
import com.spot.good2travel.repository.CategoryRepository;
import com.spot.good2travel.repository.ItemCategoryRepository;
import com.spot.good2travel.repository.ItemRepository;
import com.spot.good2travel.repository.LocalGovernmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final LocalGovernmentRepository localGovernmentRepository;
    private final CategoryRepository categoryRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemType createOfficialItem(ItemRequest.OfficialItemCreateRequest officialItemCreateRequest) {
        LocalGovernment localGovernment = localGovernmentRepository.findById(officialItemCreateRequest.getLocalGovernmentId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.LOCALGOVERNMENT_NOT_FOUND));

        //굳이인 경우에는 카테고리까지 연결
        if (officialItemCreateRequest.getType()==ItemType.GOODE){
            Item officialGoode = Item.ofGoode(officialItemCreateRequest, localGovernment);
            itemRepository.save(officialGoode);
            createItemCategory(officialItemCreateRequest, officialGoode);
            return officialGoode.getType();
        } else {
            Item officialPlan = Item.ofPlan(officialItemCreateRequest, localGovernment);
            itemRepository.save(officialPlan);
            return officialPlan.getType();
        }
    }


    private void createItemCategory(ItemRequest.OfficialItemCreateRequest officialItemCreateRequest, Item officialGoode) {
        List<Category> categoryList = officialItemCreateRequest.getCategories()
                .stream()
                .map(categoryId -> categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundElementException(ExceptionMessage.CATEGORY_NOT_FOUND)))
                .toList();

        List<ItemCategory> itemCategories = categoryList.stream()
                .map(category -> ItemCategory.of(category, officialGoode))
                .toList();

        itemCategoryRepository.saveAll(itemCategories);
    }

    public Long createItem(ItemRequest.ItemCreateRequest itemCreateRequest) {
        Item officialItem = Item.of(itemCreateRequest);
        itemRepository.save(officialItem);
        return officialItem.getId();
    }

    public Long moveItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));
        if (item.getIsOfficial()){
            return item.getId();
        } else {
            Item newItem = item.copy(); //객체 새로 생성
            itemRepository.save(newItem);
            return newItem.getId();
        }
    }

    @Transactional
    public Long updateItem(Long itemId, ItemRequest.ItemUpdateRequest itemUpdateRequest) {        
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));
        if (item.getIsOfficial()){
            throw new ItemAccessException(ExceptionMessage.ITEM_UPDATE_BAD_REQUEST);
        } else {
            LocalGovernment localGovernment = localGovernmentRepository.findById(itemUpdateRequest.getLocalGovernmentId())
                    .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.LOCALGOVERNMENT_NOT_FOUND));
            item.updateItem(itemUpdateRequest, localGovernment);
        }
        return item.getId();
    }

    @Transactional
    public ItemResponse.GoodeDetailsResponse getGoodeDetails(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));
        if (item.getType() == ItemType.GOODE && item.getIsOfficial()){
            //todo 해당 지역의 날씨 가져오기
            return ItemResponse.GoodeDetailsResponse.of(item, item.getLocalGovernment().getName());
        } else {
            throw new ItemAccessException(ExceptionMessage.ITEM_DETAIL_INFO_NOT_FOUND);
        }
    }

    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));
        if (item.getIsOfficial()){
            throw new ItemAccessException(ExceptionMessage.ITEM_DELETE_BAD_REQUEST);
        } else {
            itemRepository.deleteById(itemId);
        }
    }
}
