import { CommonModule } from '@angular/common';
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { Component } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { UploadService } from '../../services/upload.service';
import { ApiResponseDto } from '../../models/dto.model';
import { CustomAlertComponent } from '../../common-components/custom-alert/custom-alert.component';
import { MatDialog } from '@angular/material/dialog';
import { ResponseTypeColor } from '../../constants/commonConsts';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.css'
})
export class UploadComponent {
  SelectedFile: File | null = null;
  UploadProgress = 0;
  MatProgressBar = false;

  private readonly CHUNK_SIZE = 1024 * 1024; // 1 MB chunks

  constructor(
    private uploadService: UploadService,
    private dialog: MatDialog) { }

  OnFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.SelectedFile = input.files[0];
      this.UploadProgress = 0;
    }
  }

  async UploadFile(): Promise<void> {
    if (!this.SelectedFile) return;

    this.MatProgressBar = true;
    this.UploadProgress = 0;

    const file = this.SelectedFile;
    const totalChunks = Math.ceil(file.size / this.CHUNK_SIZE);

    try {
      // STEP 1 :: Initiate upload session with the server and retrieve uploadId
      const Payload = {
        fileName: file.name,
        fileSize: file.size
      }

      const InitiatedResponse: ApiResponseDto = await firstValueFrom(this.uploadService.InitiateUpload(Payload));

      if (InitiatedResponse.success !== true || InitiatedResponse.statusCode !== 200) {
        this.dialog.open(CustomAlertComponent, { data: { text: InitiatedResponse.message, type: ResponseTypeColor.ERROR } });
        this.MatProgressBar = false;
        return;
      }

      const UploadId = InitiatedResponse.data;

      if (!UploadId) {
        this.dialog.open(CustomAlertComponent, { data: { text: "Failed to initiate upload.", type: ResponseTypeColor.ERROR } });
        return;
      }


      // STEP 2 :: Loop through and upload chunks sequentially
      for (let chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
        const start = chunkIndex * this.CHUNK_SIZE;
        const end = Math.min(start + this.CHUNK_SIZE, file.size);

        const chunkBlob = file.slice(start, end);
        const arrayBuffer = await chunkBlob.arrayBuffer();

        const upload = this.uploadService.UploadChunk(arrayBuffer, chunkIndex, totalChunks, UploadId ? UploadId : '');

        await this.HandleChunkUploadProgress(upload, chunkIndex, totalChunks);
      }

      console.log('All chunks uploaded successfully under session:', UploadId);
      this.SelectedFile = null;
    } catch (error) {
      console.error('Upload sequence aborted due to an error:', error);
    } finally {
      this.MatProgressBar = false;
    }
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