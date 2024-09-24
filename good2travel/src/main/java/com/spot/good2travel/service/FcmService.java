package com.spot.good2travel.service;

import com.google.firebase.messaging.*;
import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.fcm.FcmRequest;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.Fcm;
import com.spot.good2travel.domain.Notification;
import com.spot.good2travel.domain.Post;
import com.spot.good2travel.domain.User;
import com.spot.good2travel.dto.CommentRequest;
import com.spot.good2travel.repository.FcmRepository;
import com.spot.good2travel.repository.NotificationRepository;
import com.spot.good2travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class FcmService {

    private final FcmRepository fcmRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public String sendMessage(String token, String title, String body, Long postId) throws FirebaseMessagingException {
        String postLink = "https://goode-travel.com/post/" +postId;

        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300")
                        .setNotification(new WebpushNotification(title, body))
                        .setFcmOptions(WebpushFcmOptions.withLink(postLink)) //해당 게시물로 이동인데.. 왜 인지 그냥 메인 페이지로만 갑니다 ㅜ
                        .build())
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    @Transactional
    public void updateToken(FcmRequest.FcmUpdate fcmUpdate, UserDetails userDetails){
        Long id = ((CustomUserDetails) userDetails).getId();
        Optional<Fcm> fcm = fcmRepository.findByUserId(id);
        if (fcm.isPresent()) {
            fcm.get().toUpdate(fcmUpdate.getFcmToken());
        } else {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.USER_NOT_FOUND));
            fcmRepository.save(Fcm.of(fcmUpdate.getFcmToken(), user));
        }
    }

    public void sendMessageForComment(User user, Post post, CommentRequest.CommentCreateRequest request, LocalDateTime notificationTime) throws FirebaseMessagingException {
        Fcm fcm = fcmRepository.findByUserId(post.getUser().getId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FCM_TOKEN_NOT_FOUND));
        String title = user.getNickname() + "님이 '" + post.getTitle()+"' 게시물에 댓글을 달았어요.";
        String body = request.getContent();
        sendMessage(fcm.getFcmToken(),title, body, post.getId());
        notificationRepository.save(Notification.of(post.getId(), title, body, notificationTime, post.getUser()));
    }

    public void sendMessageForReplyComment(User user, Post post, CommentRequest.ReplyCommentCreateRequest request, LocalDateTime notificationTime) throws FirebaseMessagingException {
        Fcm fcm = fcmRepository.findByUserId(post.getUser().getId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.FCM_TOKEN_NOT_FOUND));
        String title = user.getNickname() + "님이 내 댓글에 대댓글을 달았어요.";
        String body = request.getContent();
        sendMessage(fcm.getFcmToken(),title, body, post.getId());
        notificationRepository.save(Notification.of(post.getId(), title, body, notificationTime, post.getUser()));
    }

}
