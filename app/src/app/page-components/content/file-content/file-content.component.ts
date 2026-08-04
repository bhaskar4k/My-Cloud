import { Component, Input } from '@angular/core';
import { FileDetailsEntity } from '../../../models/folder.model';

@Component({
  selector: 'app-file-content',
  imports: [],
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
