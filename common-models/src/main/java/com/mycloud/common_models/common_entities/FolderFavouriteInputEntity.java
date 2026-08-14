package com.mycloud.common_models.common_entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderFavouriteInputEntity {
    private String FolderId;
    private Boolean Favourite;
}
