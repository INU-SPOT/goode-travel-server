package com.spot.good2travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;

    @Column(nullable = false)
    private String email;

    private String nickname;

    private String profileImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SmallComment> smallComments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Folder> folders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metropolitan_government")
    private MetropolitanGovernment metropolitanGovernment;


    @Builder
    public User(String provider, String email, String nickname, String profileImageUrl, List<String> role){
        this.provider = provider;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    public User updateUser(String nickname, String profileImageUrl){
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;

        return this;
    }

}
