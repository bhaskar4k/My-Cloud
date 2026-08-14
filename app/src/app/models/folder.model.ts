export interface FolderDetailsEntity {
    HasFolder: boolean;
    FolderCount: number;
    FoldersList: FolderInfoEntity[];
}

export interface FolderInfoEntity {
    FolderId: string;
    FolderName: string;
    CreatedAt?: string;
    Depth?: number;
    Favourite?: boolean;
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