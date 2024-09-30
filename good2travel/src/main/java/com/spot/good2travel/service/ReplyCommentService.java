package com.spot.good2travel.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.exception.UserNotAuthorizedException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.Comment;
import com.spot.good2travel.domain.Fcm;
import com.spot.good2travel.domain.ReplyComment;
import com.spot.good2travel.domain.User;
import com.spot.good2travel.dto.CommentRequest.CommentUpdateRequest;
import com.spot.good2travel.dto.CommentRequest.ReplyCommentCreateRequest;
import com.spot.good2travel.repository.CommentRepository;
import com.spot.good2travel.repository.FcmRepository;
import com.spot.good2travel.repository.ReplyCommentRepository;
import com.spot.good2travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyCommentService {


    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReplyCommentRepository replyCommentRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FcmService fcmService;
    private final FcmRepository fcmRepository;

    @Transactional
    public void addReplyComment(ReplyCommentCreateRequest request, UserDetails userDetails) throws FirebaseMessagingException {
        if(userDetails == null){
            throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
        }
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.USER_NOT_FOUND));
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.COMMENT_NOT_FOUND));

        ReplyComment replyComment = ReplyComment.of(request, user, comment);
        replyCommentRepository.save(replyComment);
        if (!userId.equals(comment.getPost().getUser().getId())) {
            Optional<Fcm> fcm = fcmRepository.findByUserId(comment.getPost().getUser().getId());
            if (fcm.isPresent()) {
                fcmService.sendMessageForReplyComment(user, comment.getPost(), fcm.get().getFcmToken(), request, replyComment.getCreateDate());
            }
        }
    }

    @Transactional
    public void reportReplyComment(Long commentId, UserDetails userDetails){
        if(userDetails == null){
            throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
        }

        Long userId = ((CustomUserDetails) userDetails).getId();
        String commentReportKey = "reportCommentId:" + commentId;
        ReplyComment comment = replyCommentRepository.findById(commentId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.REPLY_COMMENT_NOT_FOUND));

        if(Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(commentReportKey, userId))){
            redisTemplate.opsForSet().add(commentReportKey, userId);
            Integer beforeReport = comment.getReport();
            comment.updateReplyCommentReport(beforeReport+1);
        }
        else{
            throw new UserNotAuthorizedException(ExceptionMessage.ALREADY_REPORTED);
        }
    }

    @Transactional
    public void deleteReplyComment(Long ReplyCommentId, UserDetails userDetails){
        if(userDetails == null){
            throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
        }

        Long userId = ((CustomUserDetails) userDetails).getId();

        ReplyComment comment = replyCommentRepository.findById(ReplyCommentId)
                .orElseThrow(()->new NotFoundElementException(ExceptionMessage.REPLY_COMMENT_NOT_FOUND));

        if(!Objects.equals(comment.getUser().getId(), userId)){
            throw new UserNotAuthorizedException(ExceptionMessage.COMMENT_NOT_USERS);
        }

        replyCommentRepository.delete(comment);
    }

    @Transactional
    public void updateReplyComment(Long replyCommentId, CommentUpdateRequest request, UserDetails userDetails){
        if(userDetails == null){
            throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
        }

        Long userId = ((CustomUserDetails) userDetails).getId();

        ReplyComment comment = replyCommentRepository.findById(replyCommentId)
                .orElseThrow(()->new NotFoundElementException(ExceptionMessage.REPLY_COMMENT_NOT_FOUND));

        if(comment.getUser().getId().equals(userId)){
            comment.updateReplyComment(request);
        }
    }

    public List<ReplyComment> getUserReplyComment(UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        return replyCommentRepository.findByUserId(userId);
    }
}
