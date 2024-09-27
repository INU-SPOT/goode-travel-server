package com.spot.good2travel.domain;

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
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn(name = "order_index")
    private List<Long> sequence = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mainItem")
    private Item mainItem;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCourse> itemCourses = new ArrayList<>();

    @Builder
    public Course(List<Long> sequence, Item mainItem, List<ItemCourse> itemCourses){
        this.sequence = sequence;
        this.mainItem = mainItem;
        this.itemCourses = itemCourses;
    }

    public static Course of(){
        return Course.builder()
                .build();
    }

    public Course createCourse(Item mainItem, List<ItemCourse> itemCourses, List<Long> sequence){
        this.mainItem = mainItem;
        this.itemCourses = itemCourses;
        this.sequence = sequence;

        return this;
    }

    public Course updateCourse(List<ItemCourse> itemCourses, List<Long> sequence){
        this.itemCourses = itemCourses;
        this.sequence = sequence;

        return this;
    }

}
