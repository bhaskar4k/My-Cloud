package com.mycloud.common_models.common_entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderDetailsEntity {
    public Boolean HasFolder;
    public Integer FolderCount;
    public List<FolderInfoEntity> FoldersList;
}
