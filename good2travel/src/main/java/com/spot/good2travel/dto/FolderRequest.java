package com.spot.good2travel.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FolderRequest {

    public static class FolderCreateReqeust{
        private String title;
    }

    public static class FolderListUpdateRequest{
        private List<Integer> sequence;
    }

    public static class FolderTitleUpdateReqeust{
        private String title;
    }
}
