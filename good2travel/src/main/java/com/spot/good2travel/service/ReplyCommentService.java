package com.spot.good2travel.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.exception.UserNotAuthorizedException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.*;
import com.spot.good2travel.dto.CommentRequest;
import com.spot.good2travel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ReplyCommentService {


    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReplyCommentRepository replyCommentRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FcmService fcmService;
    private final FcmRepository fcmRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public void addReplyComment(CommentRequest.ReplyCommentCreateUpdateRequest request, UserDetails userDetails) throws FirebaseMessagingException {
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
        if (!userId.equals(user.getId())) {
            sendMessageForReplyComment(user, comment.getPost(), request, replyComment.getCreateDate());
        }
    }

    private void sendMessageForReplyComment(User user, Post post, CommentRequest.ReplyCommentCreateUpdateRequest request, LocalDateTime localDateTime) throws FirebaseMessagingException {
        Fcm fcm = fcmRepository.findByUserId(post.getUser().getId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FCM_TOKEN_NOT_FOUND));
        String title = user.getNickname() + "님이 내 댓글에 대댓글을 달았어요.";
        String body = request.getContent();
        fcmService.sendMessage(fcm.getFcmToken(),title, body);
        alarmRepository.save(Alarm.of(title, body, localDateTime, user));
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
    public void updateReplyComment(Long replyCommentId, CommentRequest.ReplyCommentCreateUpdateRequest request, UserDetails userDetails){
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
}
