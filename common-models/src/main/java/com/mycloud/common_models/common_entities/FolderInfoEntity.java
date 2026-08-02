package com.mycloud.common_models.common_entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderInfoEntity {
    public String FolderId;
    public String FolderName;
    public String CreatedAt;
    public Integer Depth;

    public FolderInfoEntity(String FolderId, String FolderName){
        this.FolderId = FolderId;
        this.FolderName = FolderName;
    }
}
