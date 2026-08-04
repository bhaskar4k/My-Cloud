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
public class FileDetailsEntity {
    public Boolean HasFile;
    public Integer FileCount;
    public List<FileInformationEntity> FilesList;
}
