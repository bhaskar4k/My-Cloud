export interface FileRenameInputEntity {
    FileId: string;
    UpdatedFileName: string;
}

export interface FileDeleteInputEntity {
    FileId: string;
}

export interface FileFavouriteInputEntity {
    FileId: string;
    Favourite: boolean;
}

export interface FileDeleteEmitEntity {
    Deleted: boolean;
    FileId: string;
}