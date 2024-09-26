package com.spot.good2travel.domain;

import com.spot.good2travel.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;

    @Column(nullable = false)
    private String email;

    private String nickname;

    private String profileImageName;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> role = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Comment> comments =new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<ReplyComment> replyComments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Folder> folders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private final List<Notification> notifications = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metropolitan_government")
    private MetropolitanGovernment metropolitanGovernment;


    @Builder
    public User(String provider, String email, String nickname, String profileImageName, List<String> role){
        this.provider = provider;
        this.email = email;
        this.nickname = nickname;
        this.profileImageName = profileImageName;
        this.role = role;
    }

    public User updateUser(String nickname, String profileImageUrl, MetropolitanGovernment metropolitanGovernment){
        this.nickname = nickname;
        this.profileImageName = profileImageUrl;
        this.metropolitanGovernment = metropolitanGovernment;
        return this;
    }

}
