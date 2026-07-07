import { CommonModule } from '@angular/common';
import { HttpClient, HttpEvent, HttpEventType } from '@angular/common/http';
import { Component } from '@angular/core';
import { UploadService } from '../../services/upload.service';

@Component({
  selector: 'app-upload',
  imports: [CommonModule],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.css'
})
export class UploadComponent {
  SelectedFile: File | null = null;
  UploadProgress = 0;
  IsUploading = false;

  private readonly CHUNK_SIZE = 1024 * 1024; // 1 MB chunks

  constructor(private uploadService: UploadService) { }

  OnFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.SelectedFile = input.files[0];
      this.UploadProgress = 0;
    }
  }

  async UploadFile(): Promise<void> {
    if (!this.SelectedFile) return;

    this.IsUploading = true;
    this.UploadProgress = 0;

    const file = this.SelectedFile;
    const totalChunks = Math.ceil(file.size / this.CHUNK_SIZE);
    const fileId = `${Date.now()}-${file.name.replace(/[^a-zA-Z0-9]/g, '')}`;

    for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
      const start = chunkIndex * this.CHUNK_SIZE;
      const end = Math.min(start + this.CHUNK_SIZE, file.size);

      const chunkBlob = file.slice(start, end);
      const arrayBuffer = await chunkBlob.arrayBuffer();

      try {
        const upload = this.uploadService.UploadChunk(arrayBuffer, chunkIndex, totalChunks, fileId, file.name);
        await this.HandleChunkUploadProgress(upload, chunkIndex, totalChunks);
      } catch (error) {
        console.error(`Upload stopped at chunk ${chunkIndex}:`, error);
        this.IsUploading = false;
        return;
      }
    }

    this.IsUploading = false;
    console.log('All byte stream chunks sent successfully!');
    this.SelectedFile = null;
  }

  private HandleChunkUploadProgress(uploadObservable: any, chunkIndex: number, totalChunks: number): Promise<any> {
    return new Promise((resolve, reject) => {
      uploadObservable.subscribe({
        next: (event: HttpEvent<any>) => {

          if (event.type === HttpEventType.UploadProgress && event.total) {
            const chunkProgress = event.loaded / event.total;
            const totalUploadedChunks = chunkIndex + chunkProgress;
            this.UploadProgress = Math.round((totalUploadedChunks / totalChunks) * 100);
          }
        },
        error: (err: any) => reject(err),
        complete: () => resolve(true)
      });
    });
  }
}
