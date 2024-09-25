package com.spot.good2travel.service;


import com.google.firebase.messaging.FirebaseMessagingException;
import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.exception.UserNotAuthorizedException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.Comment;
import com.spot.good2travel.domain.Post;
import com.spot.good2travel.domain.ReplyComment;
import com.spot.good2travel.domain.User;
import com.spot.good2travel.dto.CommentRequest;
import com.spot.good2travel.dto.CommentResponse;
import com.spot.good2travel.repository.CommentRepository;
import com.spot.good2travel.repository.PostRepository;
import com.spot.good2travel.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.spot.good2travel.dto.CommentRequest.CommentCreateRequest;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FcmService fcmService;
    private final ReplyCommentService replyCommentService;


    @Transactional
    public void addComment(CommentCreateRequest request, UserDetails userDetails) throws FirebaseMessagingException {
        if(userDetails == null){
            throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
        }
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository
                .findById(userId).orElseThrow(()-> new NotFoundElementException(ExceptionMessage.USER_NOT_FOUND));
        Post post  = postRepository
                .findById(request.getPostId()).orElseThrow(()-> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

        Comment comment = Comment.of(request, user, post);
        commentRepository.save(comment);
        if (!userId.equals(post.getUser().getId())){
            fcmService.sendMessageForComment(user, post, request, comment.getCreateDate());
        }
    }

    @Transactional
    public List<CommentResponse.CommentDetailResponse> getPostComments(Long postId, UserDetails userDetails){
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
    public List<CommentResponse.CommentDetailResponse> getCommentsForLogin(List<Comment> comments, User user){

        List<CommentResponse.CommentDetailResponse> response = comments.stream().map(
                comment -> CommentResponse.CommentDetailResponse.of(comment, comment.getUser().equals(user), comment.getReplyComments().stream()
                        .map(replyComment -> CommentResponse.ReplyCommentResponse.of(replyComment, replyComment.getUser().equals(user)))
                        .toList()
                )).toList();

        return response;
    }

    @Transactional
    public List<CommentResponse.CommentDetailResponse> getCommentsForNotLogin(List<Comment> comments){

        List<CommentResponse.CommentDetailResponse> response = comments.stream().map(
                comment -> CommentResponse.CommentDetailResponse.of(comment, false, comment.getReplyComments().stream()
                        .map(replyComment -> CommentResponse.ReplyCommentResponse.of(replyComment, false))
                        .toList()
                )).toList();

        return response;
    }

    @Transactional
    public void reportComment(Long commentId, UserDetails userDetails){
        if(userDetails == null){
            throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
        }
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
        if(userDetails == null){
            throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
        }

        Long userId = ((CustomUserDetails) userDetails).getId();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new NotFoundElementException(ExceptionMessage.COMMENT_NOT_FOUND));

        if(!Objects.equals(comment.getUser().getId(), userId)){
            throw new UserNotAuthorizedException(ExceptionMessage.COMMENT_NOT_USERS);
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest.@Valid CommentUpdateRequest request, UserDetails userDetails){
        if(userDetails == null){
            throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
        }

        Long userId = ((CustomUserDetails) userDetails).getId();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new NotFoundElementException(ExceptionMessage.REPLY_COMMENT_NOT_FOUND));

        if(comment.getUser().getId().equals(userId)){
            comment.updateComment(request);
        }
    }

    @Transactional
    public List<CommentResponse.UserCommentResponse> getUserComments(UserDetails userDetails){
        if (userDetails == null) {
            throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
        }

        Long userId = ((CustomUserDetails) userDetails).getId();

        List<Comment> comments = commentRepository.findByUserId(userId);
        List<ReplyComment> replyComments = replyCommentService.getUserReplyComment(userDetails);

        List<CommentResponse.UserCommentResponse> response = Stream.concat(
                        comments.stream()
                                .map(comment -> CommentResponse.UserCommentResponse.of("comment", comment.getPost().getId(), comment.getPost().getTitle(),
                                        comment.getUpdateDate().toLocalDate(), comment.getIsModified(), comment.getCreateDate(), comment.getContent())),
                        replyComments.stream()
                                .map(replyComment -> CommentResponse.UserCommentResponse.of("replyComment", replyComment.getComment().getPost().getId(),
                                        replyComment.getComment().getPost().getTitle(), replyComment.getUpdateDate().toLocalDate(),
                                        replyComment.getIsModified(), replyComment.getCreateDate(), replyComment.getContent()))
                ).sorted(Comparator.comparing(CommentResponse.UserCommentResponse::getCreateDate).reversed())
                .toList();

        return response;
    }

}
