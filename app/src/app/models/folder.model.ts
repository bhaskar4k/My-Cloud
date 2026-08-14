export interface FolderInfoEntity {
    FolderId: string;
    FolderName: string;
    CreatedAt?: string;
    Depth?: number;
    Favourite?: boolean;
}

export interface FolderDetailsEntity {
    HasFolder: boolean;
    FolderCount: number;
    FoldersList: FolderInfoEntity[];
}

export interface FolderRenameInputEntity {
    FolderId: string;
    UpdatedFolderName: string;
}

export interface FolderDeleteInputEntity {
    FolderId: string;
}

export interface FolderFavouriteInputEntity {
    FolderId: string;
    Favourite: boolean;
}

export interface FolderDeleteEmitEntity {
    Deleted: boolean;
    FolderId: string;
}

export interface FolderFavouriteEmitEntity {
    Favourite: boolean;
    FolderId: string;
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
    Favourite: boolean;
    CreatedAt: string;
    UploadedAgo: string;
    ModifiedAt: string | null;
    Deleted: boolean;
    DeletedAt: string | null;
    AutoDeletingAt: string | null;
}