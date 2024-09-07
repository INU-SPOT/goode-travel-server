package com.spot.good2travel.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> sequence;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<ItemFolder> itemFolders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void toUpdateSequence(List<Integer> sequence){
        this.sequence = sequence;
    }
}
