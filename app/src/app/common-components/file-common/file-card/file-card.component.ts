import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FileDetailsEntity, FileInfoEntity } from '../../../models/folder.model';

@Component({
  selector: 'app-file-card',
  imports: [
    CommonModule
  ],
  templateUrl: './file-card.component.html',
  styleUrl: './file-card.component.css'
})
export class FileCardComponent implements OnInit {
  @Input() FileInfo: FileInfoEntity = {
    Id: 0,
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: ''
  };

  File: FileInfoEntity = {
    Id: 0,
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: ''
  };

  ngOnInit(): void {
    this.File = this.FileInfo;
  }

  ViewMoreInFile(): void {

  }
}
