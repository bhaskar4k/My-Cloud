import { Component, Input } from '@angular/core';
import { FileInfoEntity } from '../../../models/folder.model';
import { DownloadService } from '../../../services/download.service';

@Component({
  selector: 'app-file-properties',
  imports: [],
  templateUrl: './file-properties.component.html',
  styleUrl: './file-properties.component.css'
})
export class FilePropertiesComponent {
  @Input() FileInfo: FileInfoEntity = {
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: ''
  };

  File: FileInfoEntity = {
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: ''
  };

  constructor(private downloadService: DownloadService) { }

  ngOnInit(): void {
    this.File = this.FileInfo;
    console.log('File Properties Component Initialized with File Info:', this.File);
  }

  ViewDetails() {

  }

  DownloadFile(): void {
    this.downloadService.DownloadSingleFile(this.File.FileId);
  }

  RenameFile() {

  }

  DeleteFile() {

  }
}
