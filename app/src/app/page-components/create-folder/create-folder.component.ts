import { Component, EventEmitter, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-create-folder',
  imports: [FormsModule],
  templateUrl: './create-folder.component.html',
  styleUrl: './create-folder.component.css'
})
export class CreateFolderComponent {
  folderName: string = '';

  constructor(
    private dialogRef: MatDialogRef<CreateFolderComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  isSaveDisabled(): boolean {
    return this.folderName.trim().length === 0;
  }

  save() {
    const trimmed = this.folderName.trim();
    if (trimmed) {
      this.dialogRef.close(trimmed);
    }
  }

  close() {
    this.dialogRef.close(null);
  }
}
