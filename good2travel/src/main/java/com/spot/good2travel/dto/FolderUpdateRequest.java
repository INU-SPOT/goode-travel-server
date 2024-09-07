package com.spot.good2travel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderUpdateRequest {
    private int type;
    private int pos;
    private String title;

}
