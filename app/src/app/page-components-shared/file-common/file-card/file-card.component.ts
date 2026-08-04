import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FileDetailsEntity, FileInfoEntity } from '../../../models/folder.model';
import { FilePropertiesComponent } from '../file-properties/file-properties.component';

@Component({
  selector: 'app-file-card',
  imports: [
    CommonModule,
    FilePropertiesComponent
  ],
  templateUrl: './file-card.component.html',
  styleUrl: './file-card.component.css'
})
export class FileCardComponent implements OnInit {
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

  ShowMore: boolean = false;

  ngOnInit(): void {
    this.File = this.FileInfo;
  }

  ViewMoreInFile(): void {
    this.ShowMore = !this.ShowMore;
  }
}
