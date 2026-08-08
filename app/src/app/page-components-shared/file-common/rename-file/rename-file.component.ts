import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { FileInfoEntity } from '../../../models/folder.model';

@Component({
  selector: 'app-rename-file',
  imports: [FormsModule],
  templateUrl: './rename-file.component.html',
  styleUrl: './rename-file.component.css'
})
export class RenameFileComponent implements OnInit {
  File: FileInfoEntity = {
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    ContentType: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: '',
    ModifiedAt: ''
  };

  constructor(
    private dialogRef: MatDialogRef<RenameFileComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FileInfoEntity
  ) {
    this.File = structuredClone(data);
  }

  ngOnInit(): void {
    this.File = structuredClone(this.data);
  }

  isSaveDisabled(): boolean {
    return this.File.OriginalName.trim().length === 0;
  }

  save() {
    const trimmed = this.File.OriginalName.trim();
    if (trimmed) {
      this.File.OriginalName = trimmed;
      this.dialogRef.close(this.File);
    }
  }

  close() {
    this.dialogRef.close(null);
  }
}
