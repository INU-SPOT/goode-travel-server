package com.spot.good2travel.domain;

import com.spot.good2travel.common.entity.BaseEntity;
import com.spot.good2travel.dto.FolderRequest;
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
public class Folder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn(name = "order_index")
    private List<Long> sequence = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<ItemFolder> itemFolders = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Folder(String title, List<ItemFolder> itemFolders,User user){
        this.title = title;
        this.user = user;
        this.itemFolders = itemFolders;
    }

    public Folder updateFolder(FolderRequest.FolderUpdateRequest folderUpdateRequest, List<Long> sequence) {
        if (folderUpdateRequest.getTitle() != null) {
            this.title = folderUpdateRequest.getTitle();
        }

        if (sequence != null && !sequence.isEmpty()) {
            this.sequence = sequence;
        }

        return this;
    }

    public static Folder of(FolderRequest.FolderCreateRequest request, User user){
        return Folder.builder()
                .title(request.getTitle())
                .user(user).build();
    }

    public Folder updateFolderSequence(List<Long> sequence){
        this.sequence = sequence;

        return this;
    }
}
