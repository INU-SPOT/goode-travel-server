package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.ItemUpdateException;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.ItemCategory;
import com.spot.good2travel.domain.ItemType;
import com.spot.good2travel.domain.LocalGovernment;
import com.spot.good2travel.dto.ItemRequest;
import com.spot.good2travel.repository.CategoryRepository;
import com.spot.good2travel.repository.ItemCategoryRepository;
import com.spot.good2travel.repository.ItemRepository;
import com.spot.good2travel.repository.LocalGovernmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final LocalGovernmentRepository localGovernmentRepository;
    private final CategoryRepository categoryRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final ItemRepository itemRepository;

    public ItemType createOfficialItem(ItemRequest.OfficialItemCreateRequest officialItemCreateRequest) {
        LocalGovernment localGovernment = localGovernmentRepository.findById(officialItemCreateRequest.getLocalGovernmentId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.LOCALGOVERNMENT_NOT_FOUND));

        //굳이인 경우에는 카테고리까지 연결
        if (officialItemCreateRequest.getType()==ItemType.GOODE){
            Item officialGoode = Item.ofGoode(officialItemCreateRequest, localGovernment);

            officialItemCreateRequest.getCategories()
                    .stream()
                    .map(categoryId -> categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundElementException(ExceptionMessage.CATEGORY_NOT_FOUND)))
                    .map(category -> itemCategoryRepository.save(ItemCategory.of(category, officialGoode)));

            itemRepository.save(officialGoode);
            return officialGoode.getType();
        } else {
            Item officialPlan = Item.ofPlan(officialItemCreateRequest);
            itemRepository.save(officialPlan);
            return officialPlan.getType();
        }
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
            throw new ItemUpdateException(ExceptionMessage.ITEM_UPDATE_BAD_REQUEST);
        } else {
            item.updateItem(itemUpdateRequest);
        }
        return item.getId();
    }
}
