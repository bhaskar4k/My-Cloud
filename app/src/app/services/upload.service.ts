import { HttpClient, HttpEvent, HttpEventType, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, firstValueFrom } from 'rxjs';
import { ApiResponseDto } from '../models/dto.model';
import { Endpoints, GetBaseURL } from '../endpoints/endpoint';

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  public IsUploading$ = new BehaviorSubject<boolean>(false);
  public UploadProgress$ = new BehaviorSubject<number>(0);
  public ActiveFileName$ = new BehaviorSubject<string>('');

  private readonly CHUNK_SIZE = 1024 * 1024 * 10; // Optimized 10 MB chunks
  private readonly MAX_ATTEMPTS = 3;

  constructor(private http: HttpClient) { }

  private InitiateUpload(payload: { fileName: string; fileSize: number; contentType: string }): Observable<ApiResponseDto> {
    return this.http.post<ApiResponseDto>(GetBaseURL() + Endpoints.Upload.Initiate, payload);
  }

  private UploadChunk(buffer: ArrayBuffer, chunkIndex: number, totalChunks: number, fileId: string): Observable<HttpEvent<any>> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/octet-stream',
      'X-Upload-Id': fileId,
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


  async StartBackgroundUpload(file: File): Promise<void> {
    this.IsUploading$.next(true);
    this.UploadProgress$.next(0);
    this.ActiveFileName$.next(file.name);

    const totalChunks = Math.ceil(file.size / this.CHUNK_SIZE);

    try {
      // Initiate upload session with the server
      const payload = {
        fileName: file.name,
        fileSize: file.size,
        contentType: file.type || 'application/octet-stream'
      };

      const initiatedResponse = await firstValueFrom(this.InitiateUpload(payload));

      if (!initiatedResponse || initiatedResponse.success !== true || initiatedResponse.statusCode !== 200 || !initiatedResponse.data) {
        throw new Error(initiatedResponse?.message || "Failed to initiate server upload session.");
      }

      const fileId = initiatedResponse.data;

      // Loop through chunk slices sequentially
      for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
        const start = chunkIndex * this.CHUNK_SIZE;
        const end = Math.min(start + this.CHUNK_SIZE, file.size);

        const chunkBlob = file.slice(start, end);
        const arrayBuffer = await chunkBlob.arrayBuffer();

        let attempts = 0;
        let success = false;

        while (attempts < this.MAX_ATTEMPTS && !success) {
          try {
            attempts++;
            const uploadObservable = this.UploadChunk(arrayBuffer, chunkIndex, totalChunks, fileId);
            await this.TrackChunkProgress(uploadObservable, chunkIndex, totalChunks);
            success = true;
          }
          catch (error) {
            if (attempts >= this.MAX_ATTEMPTS) {
              throw new Error(`Chunk ${chunkIndex} permanently failed after ${this.MAX_ATTEMPTS} attempts.`);
            }

            // Sleep 2 seconds before firing request retry
            await new Promise(res => setTimeout(res, 2000));
          }
        }
      }

      this.IsUploading$.next(false);
      this.UploadProgress$.next(100);
      this.ActiveFileName$.next('');
    }
    catch (error) {
      this.IsUploading$.next(false);
      this.UploadProgress$.next(0);
      this.ActiveFileName$.next('');
      throw error;
    }
  }

  private TrackChunkProgress(uploadObservable: Observable<any>, chunkIndex: number, totalChunks: number): Promise<any> {
    return new Promise((resolve, reject) => {
      uploadObservable.subscribe({
        next: (event: HttpEvent<any>) => {
          if (event.type === HttpEventType.UploadProgress && event.total) {
            const chunkProgress = event.loaded / event.total;
            const totalUploadedChunks = chunkIndex + chunkProgress;
            const absolutePercentage = Math.round((totalUploadedChunks / totalChunks) * 100);

            this.UploadProgress$.next(absolutePercentage);
          }
        },
        error: (err: any) => reject(err),
        complete: () => resolve(true)
      });
    });
  }
}