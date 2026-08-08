export const EndpointType = {
    dev: "http://localhost:8080/",
    prod: "https://educarecenter-in-gogy.onrender.com/"
}

export function GetBaseURL() {
    let BaseUrl = EndpointType.dev;
    // let BaseUrl = EndpointType.prod;

    return BaseUrl + "api/";
}

export const Endpoints = {
    Auth: {
        Register: "auth/user/create",
        Login: "auth/user/login",
    },
    Common: {
        GetMenu: "common/menu/get-menu",
    },
    Upload: {
        Initiate: "file/upload/initiate",
        Chunk: "file/upload/chunk"
    },
    Download: {
        SingleFile: "file/download/single-file"
    },
    Folder: {
        ValidateFolderAccess: "file/folder/validate-folder-access",
        Create: "file/folder/create",
        GetAll: "file/folder/get-all"
    },
    File: {
        GetAll: "file/file/get-all"
    }
}