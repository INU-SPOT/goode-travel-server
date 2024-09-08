package com.spot.good2travel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FolderListResponse {
    private String title;
    private String imageUrl; //해당 폴더에서 제일 처음 저장된 굳이의 사진 링크 (없을 경우 null)
}
