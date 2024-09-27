package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CourseResponse {

    @Getter
    public static class CourseDetailResponse{

        @Schema(example = "1")
        private final Long goodeId;
        @Schema(example = "굳이? 성심당가기")
        private final String goodeTitle;
        @Schema(example = "서울특별자치도")
        private final String goodeMetropolitanGovernmentName;

        private final List<ItemCourseResponse> itemCourses;

        @Builder
        public CourseDetailResponse(Long goodeId, String goodeTitle, String goodeMetropolitanGovernmentName, List<ItemCourseResponse> itemCourses){
            this.goodeId = goodeId;
            this.goodeTitle = goodeTitle;
            this.goodeMetropolitanGovernmentName = goodeMetropolitanGovernmentName;
            this.itemCourses = itemCourses;
        }

        public static CourseDetailResponse of(Item goode, List<ItemCourseResponse> itemCourses){
            return CourseDetailResponse.builder()
                    .goodeId(goode.getId())
                    .goodeTitle(goode.getTitle())
                    .goodeMetropolitanGovernmentName(goode.getLocalGovernment().getMetropolitanGovernment().getName())
                    .itemCourses(itemCourses)
                    .build();
        }
    }

    @Getter
    public static class ItemCourseResponse{

        @Schema(example = "1")
        private final Long itemId;
        @Schema(example = "남선공원 가서 산책하기")
        private final String itemTitle;
        @Schema(example = "대전 어쩌고저쩌고")
        private final String itemAddress;

        @Builder
        public ItemCourseResponse(Long itemId, String itemTitle, String itemAddress){
            this.itemId = itemId;
            this.itemTitle = itemTitle;
            this.itemAddress = itemAddress;
        }

        public static ItemCourseResponse of(Item item){
            return ItemCourseResponse.builder()
                    .itemId(item.getId())
                    .itemTitle(item.getTitle())
                    .itemAddress(item.getAddress())
                    .build();
        }
    }

}
