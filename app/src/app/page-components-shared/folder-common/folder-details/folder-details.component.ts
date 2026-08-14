import { Component, Inject, OnInit } from '@angular/core';
import { FolderInfoEntity } from '../../../models/folder.model';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { GetReadableFileSize } from '../../../common/FileUtil';

@Component({
  selector: 'app-folder-details',
  imports: [],
  templateUrl: './folder-details.component.html',
  styleUrl: './folder-details.component.css'
})
export class FolderDetailsComponent implements OnInit {
  Folder: FolderInfoEntity = {
    FolderId: '',
    FolderName: '',
    Depth: 0,
    SubFolderCount: 0,
    FilesCount: 0,
    TotalSize: 0,
    Favourite: false,
    CreatedAt: '',
    CreatedAgo: '',
    ModifiedAt: '',
    Deleted: false,
    DeletedAt: '',
    AutoDeletingAt: ''
  };

  GetReadableFileSize = GetReadableFileSize;

  constructor(
    private dialogRef: MatDialogRef<FolderDetailsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FolderInfoEntity
  ) {
    this.Folder = data;
  }

  ngOnInit(): void {
    this.Folder = this.data;
  }

  close() {
    this.dialogRef.close(null);
  }
}
