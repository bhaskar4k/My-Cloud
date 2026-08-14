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
    private Integer Depth;
    private Integer SubFolderCount;
    private Integer FilesCount;
    private Long TotalSize;
    private Boolean Favourite;
    private String CreatedAt;
    private String CreatedAgo;
    private String ModifiedAt;
    private Boolean Deleted;
    private String DeletedAt;
    private String AutoDeletingAt;

    public FolderInfoEntity(String FolderId, String FolderName){
        this.FolderId = FolderId;
        this.FolderName = FolderName;
    }
}
