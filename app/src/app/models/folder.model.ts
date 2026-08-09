export interface FolderInfoEntity {
    FolderId: string;
    FolderName: string;
    CreatedAt?: string;
    Depth?: number;
}

export interface FolderDetailsEntity {
    HasFolder: boolean;
    FolderCount: number;
    FoldersList: FolderInfoEntity[];
}

export interface FileDetailsEntity {
    HasFile: boolean;
    FileCount: number;
    FilesList: FileInfoEntity[];
}

export interface FileInfoEntity {
    FileId: string;
    OriginalName: string;
    FileExtension: string;
    ContentType: string;
    FileSize: number;
    CreatedAt: string;
    UploadedAgo: string;
    ModifiedAt: string;
}

export interface FileRenameInputEntity {
    FileId: string;
    UpdatedFileName: string;
}