package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.ItemCourseIsExistException;
import com.spot.good2travel.common.exception.ItemTypeException;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.domain.Course;
import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.ItemCourse;
import com.spot.good2travel.domain.ItemType;
import com.spot.good2travel.dto.CourseRequest;
import com.spot.good2travel.dto.CourseResponse;
import com.spot.good2travel.repository.CourseRepository;
import com.spot.good2travel.repository.ItemCourseRepository;
import com.spot.good2travel.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ItemRepository itemRepository;
    private final ItemCourseRepository itemCourseRepository;

    @Transactional
    public Long createCourse(Long itemId, CourseRequest.CourseCreateUpdateRequest request){
        Item goode = itemRepository.findById(itemId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));

        Course course = Course.of();

        validItemIsGoode(goode);
        validCourseIsExist(goode);

        List<ItemCourse> itemCourses = new ArrayList<>();
        List<Long> sequence = request.getItems().stream().map(num -> {
            Item item = itemRepository.findById(num)
                    .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));
            ItemCourse itemCourse = createItemCourse(item, course);
            itemCourses.add(itemCourse);

            return itemCourse.getId();
        }).toList();

        course.createCourse(goode, itemCourses, sequence);

        courseRepository.save(course);
        return course.getId();
    }

    @Transactional
    public ItemCourse createItemCourse(Item item, Course course){
        ItemCourse itemCourse = ItemCourse.of(item, course);
        if(item.getType().equals(ItemType.GOODE) || !item.getIsOfficial()){
            throw new ItemTypeException(ExceptionMessage.ITEM_TYPE_NOT_PLAN);
        }
        itemCourseRepository.save(itemCourse);
        return itemCourse;
    }

    @Transactional
    public Long updateCourse(Long courseId, CourseRequest.CourseCreateUpdateRequest request){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.COURSE_NOT_FOUND));

        course.getItemCourses().clear();

        List<Long> sequence = request.getItems().stream().map(num -> {
            Item item = itemRepository.findById(num)
                    .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));
            ItemCourse itemCourse = createItemCourse(item, course);
            return itemCourse.getId();
        }).toList();

        course.updateCourse(sequence);

        return courseId;
    }

    @Transactional
    public CourseResponse.CourseDetailResponse getGoodeCourse(Long itemId){
        Item goode = itemRepository.findById(itemId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));

        validItemIsGoode(goode);

        Course course = courseRepository.findByMainItem(goode)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.COURSE_NOT_FOUND));

        CourseResponse.CourseDetailResponse response = CourseResponse.CourseDetailResponse
                .of(course.getId(), goode, course.getItemCourses().stream().map(this::getItemCourseResponse).toList());

        return response;
    }

    @Transactional
    public CourseResponse.ItemCourseResponse getItemCourseResponse(ItemCourse itemCourse){
        return CourseResponse.ItemCourseResponse.of(itemCourse.getItem());
    }

    @Transactional
    public void validItemIsGoode(Item item){
        if(item.getIsOfficial() && item.getType() == ItemType.GOODE){
            return;
        }
        throw new ItemTypeException(ExceptionMessage.ITEM_TYPE_NOT_OFFICIAL_GOODE);
    }

    /**
     일단은 코스를 하나만 하기로 해서 멍청이슈를 줄이기 위해 하나만 생성되도록 lock
     */

    @Transactional
    public void validCourseIsExist(Item item){
        Optional<Course> course = courseRepository.findByMainItem(item);

        if(course.isPresent()){
            throw new ItemCourseIsExistException(ExceptionMessage.ITEM_ISEXIST_COURSE);
        }
    }

    @Transactional
    public void deleteCourse(Long courseId){
        courseRepository.deleteById(courseId);
    }
}
