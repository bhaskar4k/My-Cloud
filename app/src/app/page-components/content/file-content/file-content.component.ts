import { Component, Input } from '@angular/core';
import { FileDetailsEntity, FileInfoEntity } from '../../../models/folder.model';
import { CommonModule } from '@angular/common';
import { FileCardComponent } from '../../../page-components-shared/file-common/file-card/file-card.component';
import { FileDeleteEmitEntity } from '../../../models/file.model';

@Component({
  selector: 'app-file-content',
  imports: [
    CommonModule,
    FileCardComponent
  ],
  templateUrl: './file-content.component.html',
  styleUrl: './file-content.component.css'
})
export class FileContentComponent {
  @Input() AllFileDetails: FileDetailsEntity = { HasFile: false, FileCount: 0, FilesList: [] };

  AllFile: FileDetailsEntity = { HasFile: false, FileCount: 0, FilesList: [] };

  ngOnInit(): void {
    this.AllFile = this.AllFileDetails;
  }

  OnUpdateAnyFile(File: FileInfoEntity) {
    const index = this.AllFile.FilesList.findIndex(f => f.FileId === File.FileId);
    if (index !== -1) {
      this.AllFile.FilesList[index] = File;
    }
  }

  OnDeleteAnyFile(DeletedFile: FileDeleteEmitEntity) {
    if (DeletedFile.Deleted === false) return;

    const index = this.AllFile.FilesList.findIndex(f => f.FileId === DeletedFile.FileId);
    if (index !== -1) {
      this.AllFile.FilesList.splice(index, 1);
    }
  }
}
