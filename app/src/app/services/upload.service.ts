import { HttpClient, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private uploadUrl = 'https://your-api-endpoint.com/upload';

  constructor(private http: HttpClient) { }

  UploadChunk(
    buffer: ArrayBuffer,
    chunkIndex: number,
    totalChunks: number,
    fileId: string,
    fileName: string
  ): Observable<HttpEvent<any>> {

    const headers = new HttpHeaders({
      'Content-Type': 'application/octet-stream',
      'X-File-Id': fileId,
      'X-File-Name': encodeURIComponent(fileName),
      'X-Chunk-Index': chunkIndex.toString(),
      'X-Total-Chunks': totalChunks.toString()
    });

    return this.http.post(this.uploadUrl, buffer, {
      headers,
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }
}
