package com.spot.good2travel.domain;

import com.spot.good2travel.common.entity.BaseEntity;
import com.spot.good2travel.dto.FolderRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Folder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Integer> sequence;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<ItemFolder> itemFolders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_goode_id")
    private Item mainGoode;

    @Builder
    public Folder(String title, User user){
        this.title = title;
        this.user = user;
    }

    public void updateFolder(FolderRequest .FolderUpdateRequest folderUpdateRequest){
        this.sequence = folderUpdateRequest.getSequence();
        this.title = folderUpdateRequest.getTitle();
    }

    public void updateMainGoode(Item item) {
        this.mainGoode = item;
    }
}
