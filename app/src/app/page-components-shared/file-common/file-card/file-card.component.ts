import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FileDetailsEntity, FileInfoEntity } from '../../../models/folder.model';
import { FilePropertiesComponent } from '../file-properties/file-properties.component';
import { Subscription } from 'rxjs';
import { FileMenuStateService } from '../../../services/file-menu-state.service';
import { GetFileIcon, GetFileType } from '../../../common/FileUtil';

@Component({
  selector: 'app-file-card',
  imports: [
    CommonModule,
    FilePropertiesComponent
  ],
  templateUrl: './file-card.component.html',
  styleUrl: './file-card.component.css'
})
export class FileCardComponent implements OnInit, OnDestroy {
  @Input() FileInfo: FileInfoEntity = {
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    ContentType: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: ''
  };

  File: FileInfoEntity = {
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    ContentType: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: ''
  };

  ShowMore: boolean = false;
  private sub: Subscription;

  GetFileType = GetFileType;
  GetFileIcon = GetFileIcon;

  constructor(
    private menuState: FileMenuStateService
  ) {
    this.sub = this.menuState.openId.subscribe(id => {
      this.ShowMore = id === this.fileId;
    });
  }

  get fileId(): string {
    return this.File?.FileId;
  }

  ngOnInit(): void {
    this.File = this.FileInfo;
  }

  ViewMoreInFile(event: MouseEvent): void {
    event.stopPropagation();
    this.menuState.toggle(this.fileId);
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }
}
