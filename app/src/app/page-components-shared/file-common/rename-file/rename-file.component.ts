import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-rename-file',
  imports: [FormsModule],
  templateUrl: './rename-file.component.html',
  styleUrl: './rename-file.component.css'
})
export class RenameFileComponent implements OnInit {
  fileName: string = '';

  constructor(
    private dialogRef: MatDialogRef<RenameFileComponent>,
    @Inject(MAT_DIALOG_DATA) public data: string
  ) {
    this.fileName = data;
  }

  ngOnInit(): void {
    this.fileName = this.data;
  }

  isSaveDisabled(): boolean {
    return this.fileName.trim().length === 0;
  }

  save() {
    const trimmed = this.fileName.trim();
    if (trimmed) {
      this.dialogRef.close(trimmed);
    }
  }

  close() {
    this.dialogRef.close(null);
  }
}
