import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Endpoints, GetBaseURL } from '../endpoints/endpoint';
import { Observable } from 'rxjs';
import { ApiResponseDto } from '../models/dto.model';
import { FileRenameInputEntity, FolderInfoEntity } from '../models/folder.model';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  constructor(private http: HttpClient) { }

  RenameFile(Payload: FileRenameInputEntity): Observable<ApiResponseDto> {
    return this.http.post<ApiResponseDto>(GetBaseURL() + Endpoints.File.Rename, Payload);
  }
}
