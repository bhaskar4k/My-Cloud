import { Component, Input } from '@angular/core';
import { FileDetailsEntity, FileInfoEntity } from '../../../models/folder.model';
import { CommonModule } from '@angular/common';
import { FileCardComponent } from '../../../page-components-shared/file-common/file-card/file-card.component';

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
}
