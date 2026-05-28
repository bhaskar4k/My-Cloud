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
    Common: {
        GetMenu: "common/menu/get-menu",
    },
}