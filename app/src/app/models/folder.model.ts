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