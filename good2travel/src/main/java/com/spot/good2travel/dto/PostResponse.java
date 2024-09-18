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

        @Schema(example = "깨구링")
        private final String writerNickname;

        @Schema(example = "https://image-server.squidjiny.com/frog.jpeg")
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

        @Schema(example = "3")
        private final Long visitNum;

        @Schema(example = "4")
        private final Integer likeNum;

        @Schema(example = "5")
        private final Long commentNum;

        @Schema(example = "true")
        private final Boolean isPushLike;

        @Schema(example = "false")
        private final Boolean isOwner;

        private final List<ItemPostResponse> itemPosts;

        @Builder(access = AccessLevel.PRIVATE)
        public PostDetailResponse(Long writerId, String writerNickname, String writerImageUrl, Long postId, String title, String firstContent, String lastContent,
                                  LocalDate startDate, LocalDate endDate, Long visitNum, Integer likeNum, Long commentNum, Boolean isPushLike, Boolean isOwner, List<ItemPostResponse> itemPosts){
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
            this.commentNum = commentNum;
            this.isPushLike = isPushLike;
            this.isOwner = isOwner;
            this.itemPosts = itemPosts;
        }

        public static PostDetailResponse of(Post post, Long visitNum, String writerImageUrl,
                                            Integer likeNum, Long commentNum,Boolean isPushLike, Boolean isOwner, List<ItemPostResponse> itemPosts){
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
                    .commentNum(commentNum)
                    .isPushLike(isPushLike)
                    .isOwner(isOwner)
                    .itemPosts(itemPosts)
                    .build();
        }
    }

    @Getter
    public static class ItemPostResponse{

        @Schema(example = "1")
        private final Long itemPostId;

        @Schema(example = "1")
        private final Long itemId;

        @Schema(example = "Good Eats Restaurant")
        private final String itemTitle;

        @Schema(example = "0")
        private final Integer itemType;

        @Schema(example = "true")
        private final Boolean isOfficial;

        @Schema(example = "사진을 찍었는데 저작권에 걸려서 제가 좋아하는 개구리 사진으로 대체하겠습니다ㅠㅠ")
        private final String content;

        private final List<ItemPostImageResponse> images;

        @Builder(access = AccessLevel.PRIVATE)
        public ItemPostResponse(Long itemPostId, Long itemId, String itemTitle, Integer itemType, Boolean isOfficial,
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
                    .itemType(itemPost.getItem().getType().ordinal())
                    .isOfficial(itemPost.getItem().getIsOfficial())
                    .content(itemPost.getContent())
                    .images(itemPostImageResponses)
                    .build();
        }
    }

    @Getter
    public static class ItemPostImageResponse{

        @Schema(example = "13")
        private final Long itemPostImageId;

        @Schema(example = "frog.jpeg")
        private final String imageName;

        public ItemPostImageResponse(Long itemPostImageId, String imageName){
            this.itemPostImageId = itemPostImageId;
            this.imageName = imageName;
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

        @Schema(example = "https://image-server.squidjiny.com/files/profile/https://example.com/image.jpg")
        private final String imageUrl;

        private final List<ItemPostThumbnailResponse> items;

        @Schema(example = "23")
        private final Long commentNum;

        @Schema(example = "4")
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

        @Schema(example = "0")
        private final Integer itemType;

        @Schema(example = "굳이? 집에가서 롤하기")
        private final String itemTitle;

        public ItemPostThumbnailResponse(Integer itemType, String itemTitle){
            this.itemType = itemType;
            this.itemTitle = itemTitle;
        }

        public static ItemPostThumbnailResponse of(ItemPost itemPost){
            return new ItemPostThumbnailResponse(itemPost.getItem().getType().ordinal(), itemPost.getItem().getTitle());
        }
    }

    @Getter
    public static class TopPostResponse{

        @Schema(example = "like")
        private final String topType;

        @Schema(example = "ㅈㅎㄱ")
        private final String writerName;

        @Schema(example = "3")
        private final Long postId;

        @Schema(example = "환기리의 꿀잼 인천여행")
        private final String title;

        private final List<ItemPostThumbnailResponse> items;

        @Schema(example = "4")
        private final Long topNum;

        @Builder(access = AccessLevel.PRIVATE)
        public TopPostResponse(String writerName, Long postId, String title
                , List<ItemPostThumbnailResponse> items, String topType, Long topNum){
            this.writerName = writerName;
            this.postId = postId;
            this.title = title;
            this.topType = topType;
            this.items = items;
            this.topNum = topNum;
        }

        public static TopPostResponse of(Post post, String topType,Long topNum, List<ItemPostThumbnailResponse> items){
            return TopPostResponse.builder()
                    .writerName(post.getUser().getNickname())
                    .postId(post.getId())
                    .title(post.getTitle())
                    .items(items)
                    .topType(topType)
                    .topNum(topNum)
                    .build();
        }
    }

}
