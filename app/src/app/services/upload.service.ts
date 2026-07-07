import { HttpClient, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponseDto } from '../models/dto.model';
import { Endpoints, GetBaseURL } from '../endpoints/endpoint';

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  constructor(private http: HttpClient) { }

  InitiateUpload(Payload: any): Observable<ApiResponseDto> {
    return this.http.post<ApiResponseDto>(GetBaseURL() + Endpoints.Upload.Initiate, Payload);
  }

  UploadChunk(buffer: ArrayBuffer, chunkIndex: number, totalChunks: number, UploadId: string): Observable<HttpEvent<any>> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/octet-stream',
      'X-Upload-Id': UploadId,
      'X-Chunk-Index': chunkIndex.toString(),
      'X-Total-Chunks': totalChunks.toString()
    });

    return this.http.post(GetBaseURL() + Endpoints.Upload.Chunk, buffer, {
      headers,
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }
}