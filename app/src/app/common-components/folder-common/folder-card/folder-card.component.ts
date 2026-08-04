import { Component, Input, OnInit } from '@angular/core';
import { FolderInfoEntity } from '../../../models/folder.model';

@Component({
  selector: 'app-folder-card',
  imports: [],
  templateUrl: './folder-card.component.html',
  styleUrl: './folder-card.component.css'
})
export class FolderCardComponent implements OnInit {
  @Input() FolderInfo: FolderInfoEntity = {
    FolderId: '',
    FolderName: '',
    CreatedAt: '',
    Depth: 0
  };

  Folder: FolderInfoEntity = {
    FolderId: '',
    FolderName: '',
    CreatedAt: '',
    Depth: 0
  };

  ngOnInit(): void {
    this.Folder = this.FolderInfo;
  }

  NavigateToFolder() {
    window.location.href = "/content/" + this.Folder.FolderId;
  }

  ViewMoreInFolder() {

  }
}
