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
    Favourite: boolean;
    CreatedAt: string;
    UploadedAgo: string;
    ModifiedAt: string | null;
    Deleted: boolean;
    DeletedAt: string | null;
    AutoDeletingAt: string | null;
}