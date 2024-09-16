package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.ItemCategory;
import com.spot.good2travel.domain.LocalGovernment;
import com.spot.good2travel.dto.ItemRequest;
import com.spot.good2travel.repository.CategoryRepository;
import com.spot.good2travel.repository.ItemCategoryRepository;
import com.spot.good2travel.repository.ItemRepository;
import com.spot.good2travel.repository.LocalGovernmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final LocalGovernmentRepository localGovernmentRepository;
    private final CategoryRepository categoryRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final ItemRepository itemRepository;

    public void createOfficialItem(ItemRequest.ItemCreateRequest itemCreateRequest) {
        LocalGovernment localGovernment = localGovernmentRepository.findById(itemCreateRequest.getLocalGovernmentId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.LOCALGOVERNMENT_NOT_FOUND));

        Item officialItem = Item.of(itemCreateRequest, localGovernment);

        itemCreateRequest.getCategories()
                .stream()
                .map(categoryId -> categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundElementException(ExceptionMessage.CATEGORY_NOT_FOUND)))
                .map(category -> itemCategoryRepository.save(ItemCategory.builder().category(category).item(officialItem).build()));

        itemRepository.save(officialItem);

    }
}
