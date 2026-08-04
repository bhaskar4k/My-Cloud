import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Endpoints, GetBaseURL } from '../endpoints/endpoint';
import { Observable } from 'rxjs';
import { ApiResponseDto } from '../models/dto.model';
import { FolderInfoEntity } from '../models/folder.model';

@Injectable({
  providedIn: 'root'
})
export class FolderService {
  constructor(private http: HttpClient) { }

  ValidateFolderAccess(FolderId: string): Observable<ApiResponseDto> {
    return this.http.get<ApiResponseDto>(GetBaseURL() + Endpoints.Folder.ValidateFolderAccess + `/${FolderId}`);
  }

  CreateFolder(Payload: FolderInfoEntity): Observable<ApiResponseDto> {
    return this.http.post<ApiResponseDto>(GetBaseURL() + Endpoints.Folder.Create, Payload);
  }

  GetAllChildFoldersByFolderId(FolderId: string): Observable<ApiResponseDto> {
    return this.http.get<ApiResponseDto>(GetBaseURL() + Endpoints.Folder.GetAll + `/${FolderId}`);
  }

  GetAllChildFilesByFolderId(FolderId: string): Observable<ApiResponseDto> {
    return this.http.get<ApiResponseDto>(GetBaseURL() + Endpoints.File.GetAll + `/${FolderId}`);
  }
}
