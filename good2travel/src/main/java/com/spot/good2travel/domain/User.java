package com.spot.good2travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.engine.internal.Cascade;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<String> role;

    private String profileImageUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BigComment> bigComments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SmallComment> smallComments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Folder> folders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metropolitan_government")
    private MetropolitanGovernment metropolitanGovernment;

}
