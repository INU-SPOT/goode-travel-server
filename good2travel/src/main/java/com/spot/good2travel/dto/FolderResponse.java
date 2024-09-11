package com.spot.good2travel.dto;

import com.spot.good2travel.dto.record.Goode;
import com.spot.good2travel.dto.record.Plan;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FolderResponse {

    @Getter
    @Setter
    public static class FolderListResponse{
        private String title;
        private String imageUrl; //해당 폴더에서 제일 처음 저장된 굳이의 사진 링크 (없을 경우 null)

        public FolderListResponse(String title, String imageUrl) {
        }
    }

    @Getter
    @Setter
    public static class ItemListResponse {
        List<Goode> goodes;
        List<Plan> plans;
    }

}
