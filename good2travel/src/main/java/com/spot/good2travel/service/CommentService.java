package com.spot.good2travel.service;


import com.google.firebase.messaging.FirebaseMessagingException;
import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.exception.UserNotAuthorizedException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.*;
import com.spot.good2travel.dto.CommentRequest;
import com.spot.good2travel.repository.CommentRepository;
import com.spot.good2travel.repository.FcmRepository;
import com.spot.good2travel.repository.PostRepository;
import com.spot.good2travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.spot.good2travel.dto.CommentRequest.CommentCreateRequest;
import static com.spot.good2travel.dto.CommentResponse.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FcmService fcmService;
    private final ReplyCommentService replyCommentService;
    private final FcmRepository fcmRepository;
    private final ImageService imageService;


    @Transactional
    public void addComment(CommentCreateRequest request, UserDetails userDetails) throws FirebaseMessagingException {
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository
                .findById(userId).orElseThrow(()-> new NotFoundElementException(ExceptionMessage.USER_NOT_FOUND));
        Post post  = postRepository
                .findById(request.getPostId()).orElseThrow(()-> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

        Comment comment = Comment.of(request, user, post);
        commentRepository.save(comment);
        if (!userId.equals(post.getUser().getId())){
            Optional<Fcm> fcm = fcmRepository.findByUserId(post.getUser().getId());
            if (fcm.isPresent()) {
                fcmService.sendMessageForComment(user, post, fcm.get().getFcmToken(), request, comment.getCreateDate());
            }
        }
    }

    @Transactional
    public List<CommentDetailResponse> getPostComments(Long postId, UserDetails userDetails){
        List<Comment> comments  = commentRepository.findAllByPostId(postId);

        if(userDetails != null){
            Long userId = ((CustomUserDetails) userDetails).getId();

            User user = userRepository
                    .findById(userId).orElseThrow(()-> new NotFoundElementException(ExceptionMessage.USER_NOT_FOUND));
            return getCommentsForLogin(comments, user);
        }

        return getCommentsForNotLogin(comments);
    }

    @Transactional
    public List<CommentDetailResponse> getCommentsForLogin(List<Comment> comments, User user){

        List<CommentDetailResponse> response = comments.stream().map(
                comment -> {
                    String commentImageName = comment.getUser().getProfileImageName() != null ? comment.getUser().getProfileImageName() : imageService.getDefaultUserImageName();
                    return CommentDetailResponse.of(comment, commentImageName,comment.getUser().equals(user), comment.getReplyComments().stream()
                            .map(replyComment -> {
                                String replyCommentImageName = replyComment.getUser().getProfileImageName() != null ? replyComment.getUser().getProfileImageName() : imageService.getDefaultUserImageName();
                                return ReplyCommentResponse.of(replyComment, replyCommentImageName, replyComment.getUser().equals(user));
                            })
                            .toList()
                    );
                }).toList();

        return response;
    }

    @Transactional
    public List<CommentDetailResponse> getCommentsForNotLogin(List<Comment> comments){

        List<CommentDetailResponse> response = comments.stream().map(
                comment -> {
                    String commentImageName = comment.getUser().getProfileImageName() != null ? comment.getUser().getProfileImageName() : imageService.getDefaultUserImageName();
                    return CommentDetailResponse.of(comment, commentImageName, false, comment.getReplyComments().stream()
                            .map(replyComment -> {
                                String replyCommentImageName = replyComment.getUser().getProfileImageName() != null ? replyComment.getUser().getProfileImageName() : imageService.getDefaultUserImageName();
                                return ReplyCommentResponse.of(replyComment, replyCommentImageName, false);
                            }).toList()
                    );
                }).toList();

        return response;
    }

    @Transactional
    public void reportComment(Long commentId, UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();
        String commentReportKey = "commentId:" + commentId;
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.COMMENT_NOT_FOUND));

        if(Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(commentReportKey, userId))){
            redisTemplate.opsForSet().add(commentReportKey, userId);
            Integer beforeReport = comment.getReport();
            comment.updateCommentReport(beforeReport+1);
        }
        else{
            throw new UserNotAuthorizedException(ExceptionMessage.ALREADY_REPORTED);
        }
    }

    @Transactional
    public void deleteComment(Long commentId, UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new NotFoundElementException(ExceptionMessage.COMMENT_NOT_FOUND));

        if(!Objects.equals(comment.getUser().getId(), userId)){
            throw new UserNotAuthorizedException(ExceptionMessage.COMMENT_NOT_USERS);
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest.CommentUpdateRequest request, UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new NotFoundElementException(ExceptionMessage.REPLY_COMMENT_NOT_FOUND));

        if(comment.getUser().getId().equals(userId)){
            comment.updateComment(request);
        }
    }

    @Transactional
    public List<UserCommentResponse> getUserComments(UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        List<Comment> comments = commentRepository.findByUserId(userId);
        List<ReplyComment> replyComments = replyCommentService.getUserReplyComment(userDetails);

        List<UserCommentResponse> response = Stream.concat(
                        comments.stream()
                                .map(comment -> UserCommentResponse.of("comment", comment.getPost().getId(), comment.getPost().getTitle(),
                                        comment.getUpdateDate().toLocalDate(), comment.getIsModified(), comment.getCreateDate(), comment.getContent())),
                        replyComments.stream()
                                .map(replyComment -> UserCommentResponse.of("replyComment", replyComment.getComment().getPost().getId(),
                                        replyComment.getComment().getPost().getTitle(), replyComment.getUpdateDate().toLocalDate(),
                                        replyComment.getIsModified(), replyComment.getCreateDate(), replyComment.getContent()))
                ).sorted(Comparator.comparing(UserCommentResponse::getCreateDate).reversed())
                .toList();

        return response;
    }

}
