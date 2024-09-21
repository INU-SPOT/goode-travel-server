package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PostRequest {

    @Getter
    public static class PostCreateUpdateRequest {

        @Schema(example = "환기리의 꿀잼 인천여행")
        @NotBlank(message = "제목을 입력해주세요.")
        private String title;

        @Schema(example = "안녕하세요 여러분~ 꿀잼 여행기록 가져왔습니다~")
        private String firstContent;

        @Schema(example = "이상입니다! 다녀오신 분들 후기 남겨주세요🌟🌟🌟🌟🌟🌟")
        private String lastContent;

        @Schema(example = "2019-03-01")
        @NotNull(message = "여행 시작 날짜를 입력해주세요.")
        private LocalDate startDate;

        @Schema(example = "2024-09-12")
        @NotNull(message = "여행이 끝난 날짜를 입력해주세요.")
        private LocalDate endDate;

        @Valid
        @NotNull(message = "게시글에 굳이/계획이 하나도 없습니다.")
        private List<ItemPostCreateUpdateRequest> itemPosts;

    }

    @Getter
    public static class ItemPostCreateUpdateRequest {

        @Schema(example = "1")
        private Long itemPostId;

        @Schema(example = "1")
        @NotNull(message = "itemId 입력은 필수입니다.")
        private Long itemId;

        @Schema(example = "사진을 찍었는데 저작권에 걸려서 제가 좋아하는 개구리 사진으로 대체하겠습니다ㅠㅠ")
        private String content;

        @Valid
        private List<ItemPostImageRequest> images;
    }

    @Getter
    public static class ItemPostImageRequest{

        @Schema(example = "1")
        private Long itemPostImageId;

        @Schema(example = "frog.jpeg")
        @NotNull(message = "사진의 파일명을 입력 해 주세요.")
        private String imageName;

    }

}
