package com.spot.good2travel.dto;

import com.spot.good2travel.domain.ItemPost;
import com.spot.good2travel.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class PostResponse {

    @Getter
    public static class PostDetailResponse{

        @Schema(example = "1")
        private final Long writerId;

        private final String writerNickname;

        private final String writerImageUrl;

        @Schema(example = "3")
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

        private final Long visitNum;

        private final Integer likeNum;

        private final Boolean isPushLike;

        private final Boolean isOwner;

        private final List<ItemPostResponse> itemPosts;

        @Builder(access = AccessLevel.PRIVATE)
        public PostDetailResponse(Long writerId, String writerNickname, String writerImageUrl, Long postId, String title, String firstContent, String lastContent,
                                  LocalDate startDate, LocalDate endDate, Long visitNum, Integer likeNum, Boolean isPushLike, Boolean isOwner, List<ItemPostResponse> itemPosts){
            this.writerId = writerId;
            this.writerNickname = writerNickname;
            this.writerImageUrl = writerImageUrl;
            this.postId = postId;
            this.title = title;
            this.firstContent = firstContent;
            this.lastContent = lastContent;
            this.startDate = startDate;
            this.endDate = endDate;
            this.visitNum = visitNum;
            this.likeNum = likeNum;
            this.isPushLike = isPushLike;
            this.isOwner = isOwner;
            this.itemPosts = itemPosts;
        }

        public static PostDetailResponse of(Post post, Long visitNum, String writerImageUrl,
                                            Integer likeNum, Boolean isPushLike, Boolean isOwner, List<ItemPostResponse> itemPosts){
            return PostDetailResponse.builder()
                    .writerId(post.getUser().getId())
                    .writerNickname(post.getUser().getNickname())
                    .writerImageUrl(writerImageUrl)
                    .postId(post.getId())
                    .title(post.getTitle())
                    .firstContent(post.getFirstContent())
                    .lastContent(post.getLastContent())
                    .startDate(post.getStartDate())
                    .endDate(post.getEndDate())
                    .visitNum(visitNum)
                    .likeNum(likeNum)
                    .isPushLike(isPushLike)
                    .isOwner(isOwner)
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

        @Schema(example = "PLAN")
        private final String itemType;

        @Schema(example = "true")
        private final Boolean isOfficial;

        @Schema(example = "사진을 찍었는데 저작권에 걸려서 제가 좋아하는 개구리 사진으로 대체하겠습니다ㅠㅠ")
        private final String content;

        private final List<ItemPostImageResponse> images;

        @Builder(access = AccessLevel.PRIVATE)
        public ItemPostResponse(Long itemPostId, Long itemId, String itemTitle, String itemType, Boolean isOfficial,
                                String content, List<ItemPostImageResponse> images){
            this.itemPostId = itemPostId;
            this.itemId = itemId;
            this.itemTitle = itemTitle;
            this.itemType = itemType;
            this.isOfficial = isOfficial;
            this.content = content;
            this.images = images;
        }

        public static ItemPostResponse of(ItemPost itemPost, List<ItemPostImageResponse> itemPostImageResponses){
            return ItemPostResponse.builder()
                    .itemPostId(itemPost.getId())
                    .itemId(itemPost.getItem().getId())
                    .itemTitle(itemPost.getItem().getTitle())
                    .itemType(itemPost.getItem().getType().name())
                    .isOfficial(itemPost.getItem().getIsOfficial())
                    .content(itemPost.getContent())
                    .images(itemPostImageResponses)
                    .build();
        }
    }

    @Getter
    public static class ItemPostImageResponse{

        private final Long itemPostImageId;

        @Schema(example = "https://image-server.squidjiny.com/frog.jpeg")
        private final String imageUrl;

        public ItemPostImageResponse(Long itemPostImageId, String imageUrl){
            this.itemPostImageId = itemPostImageId;
            this.imageUrl = imageUrl;
        }

        public static ItemPostImageResponse of(Long itemPostImageId, String imageUrl){
            return new ItemPostImageResponse(itemPostImageId, imageUrl);
        }

    }

    @Getter
    public static class PostThumbnailResponse{

        @Schema(example = "ㅈㅎㄱ")
        private final String writerName;

        @Schema(example = "3")
        private final Long postId;

        @Schema(example = "환기리의 꿀잼 인천여행")
        private final String title;

        private final String imageUrl;

        private final List<ItemPostThumbnailResponse> items;

        private final Long commentNum;

        private final Integer likeNum;

        @Builder(access = AccessLevel.PRIVATE)
        public PostThumbnailResponse(String writerName, Long postId, String title, String imageUrl
                , List<ItemPostThumbnailResponse> items, Long commentNum, Integer likeNum){
            this.writerName = writerName;
            this.postId = postId;
            this.title = title;
            this.imageUrl = imageUrl;
            this.items = items;
            this.commentNum = commentNum;
            this.likeNum = likeNum;
        }

        public static PostThumbnailResponse of(Post post, Integer likeNum, Long commentNum, String imageUrl, List<ItemPostThumbnailResponse> items){
            return PostThumbnailResponse.builder()
                    .writerName(post.getUser().getNickname())
                    .postId(post.getId())
                    .title(post.getTitle())
                    .imageUrl(imageUrl)
                    .items(items)
                    .commentNum(commentNum)
                    .likeNum(likeNum)
                    .build();
        }
    }

    @Getter
    public static class ItemPostThumbnailResponse{

        private final String itemType;

        private final String itemTitle;

        public ItemPostThumbnailResponse(String itemType, String itemTitle){
            this.itemType = itemType;
            this.itemTitle = itemTitle;
        }

        public static ItemPostThumbnailResponse of(ItemPost itemPost){
            return new ItemPostThumbnailResponse(itemPost.getItem().getType().name(), itemPost.getItem().getTitle());
        }
    }

}
