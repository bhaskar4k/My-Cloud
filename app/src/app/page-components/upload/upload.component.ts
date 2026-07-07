import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { UploadService } from '../../services/upload.service';
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
  MatProgressBar = false;

  constructor(
    public uploadService: UploadService,
    private dialog: MatDialog
  ) { }

  OnFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.SelectedFile = input.files[0];
    }
  }

  async UploadFile(): Promise<void> {
    if (!this.SelectedFile) return;

    this.MatProgressBar = true;
    const fileToUpload = this.SelectedFile;

    this.SelectedFile = null;

    try {
      await this.uploadService.StartBackgroundUpload(fileToUpload);

      this.dialog.open(CustomAlertComponent, {
        data: { text: "File has been uploaded successfully.", type: ResponseTypeColor.SUCCESS }
      });
    }
    catch (error: any) {
      this.dialog.open(CustomAlertComponent, {
        data: { text: "Failed to upload the selected file.<br>Please try again later.", type: ResponseTypeColor.ERROR }
      });
    }
    finally {
      this.MatProgressBar = false;
    }
  }
}