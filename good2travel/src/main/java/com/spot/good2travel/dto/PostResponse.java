package com.spot.good2travel.dto;

import com.spot.good2travel.domain.ItemPost;
import com.spot.good2travel.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PostResponse {

    @Getter
    public static class PostDetailResponse{
        private final Long postId;

        @Schema(example = "환기리의 꿀잼 인천여행")
        private final String title;

        @Schema(example = "안녕하세요 여러분~ 꿀잼 여행기록 가져왔습니다~")
        private final String firstContent;

        @Schema(example = "이상입니다! 다녀오신 분들 후기 남겨주세요🌟🌟🌟🌟🌟🌟")
        private final String lastContent;

        @Schema(example = "2019-03-01")
        private final LocalDate startDate;

        @Schema(example = "2024-09-12")
        private final LocalDate endDate;

        private final List<ItemPostResponse> itemPosts;

        @Builder
        public PostDetailResponse(Long postId, String title, String firstContent, String lastContent,
                                  LocalDate startDate, LocalDate endDate, List<ItemPostResponse> itemPosts){
            this.postId = postId;
            this.title = title;
            this.firstContent = firstContent;
            this.lastContent = lastContent;
            this.startDate = startDate;
            this.endDate = endDate;
            this.itemPosts = itemPosts;
        }

        public static PostDetailResponse of(Post post, List<ItemPostResponse> itemPosts){
            return PostDetailResponse.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .firstContent(post.getFirstContent())
                    .lastContent(post.getLastContent())
                    .startDate(post.getStartDate())
                    .endDate(post.getEndDate())
                    .itemPosts(itemPosts)
                    .build();
        }
    }

    @Getter
    public static class ItemPostResponse{

        private final Long itemPostId;

        @Schema(example = "1")
        private final Long itemId;

        @Schema(example = "Good Eats Restaurant")
        private final String itemTitle;

        @Schema(example = "사진을 찍었는데 저작권에 걸려서 제가 좋아하는 개구리 사진으로 대체하겠습니다ㅠㅠ")
        private final String content;

        private final List<ItemPostImageResponse> images;

        @Builder
        public ItemPostResponse(Long itemPostId, Long itemId, String itemTitle, String content, List<ItemPostImageResponse> images){
            this.itemPostId = itemPostId;
            this.itemId = itemId;
            this.itemTitle = itemTitle;
            this.content = content;
            this.images = images;
        }

        public static ItemPostResponse of(ItemPost itemPost, List<ItemPostImageResponse> itemPostImageResponses){
            return ItemPostResponse.builder()
                    .itemPostId(itemPost.getId())
                    .itemId(itemPost.getItem().getId())
                    .itemTitle(itemPost.getItem().getTitle())
                    .content(itemPost.getContent())
                    .images(itemPostImageResponses)
                    .build();

        }
    }

    @Getter
    public static class ItemPostImageResponse{

        @Schema(example = "https://image-server.squidjiny.com/frog.jpeg")
        private final String imageName;

        public ItemPostImageResponse(String imageName){
            this.imageName = imageName;
        }

        public static ItemPostImageResponse of(String imageName){
            return new ItemPostImageResponse(imageName);
        }

    }

    public static class PostThumbnailResponse{

    }

}
