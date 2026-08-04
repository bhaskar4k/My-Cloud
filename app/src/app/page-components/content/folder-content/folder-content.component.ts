import { Component, Input, OnInit } from '@angular/core';
import { FolderDetailsEntity, FolderInfoEntity } from '../../../models/folder.model';
import { CommonModule } from '@angular/common';
import { FolderCardComponent } from '../../../page-components-shared/folder-common/folder-card/folder-card.component';

@Component({
  selector: 'app-folder-content',
  imports: [
    CommonModule,
    FolderCardComponent
  ],
  templateUrl: './folder-content.component.html',
  styleUrl: './folder-content.component.css'
})
export class FolderContentComponent implements OnInit {
  @Input() AllFolderDetails: FolderDetailsEntity = { HasFolder: false, FolderCount: 0, FoldersList: [] };

  AllFolder: FolderDetailsEntity = { HasFolder: false, FolderCount: 0, FoldersList: [] };

  ngOnInit(): void {
    this.AllFolder = this.AllFolderDetails;
  }
}
