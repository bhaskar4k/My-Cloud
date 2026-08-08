import { Component, ElementRef, HostListener, Input } from '@angular/core';
import { FileInfoEntity } from '../../../models/folder.model';
import { DownloadService } from '../../../services/download.service';
import { FileMenuStateService } from '../../../services/file-menu-state.service';

@Component({
  selector: 'app-file-properties',
  imports: [],
  templateUrl: './file-properties.component.html',
  styleUrl: './file-properties.component.css'
})
export class FilePropertiesComponent {
  @Input() FileInfo: FileInfoEntity = {
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: ''
  };

  File: FileInfoEntity = {
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: ''
  };

  constructor(
    private downloadService: DownloadService,
    private elementRef: ElementRef,
    private menuState: FileMenuStateService
  ) { }

  ngOnInit(): void {
    this.File = this.FileInfo;
    console.log('File Properties Component Initialized with File Info:', this.File);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.menuState.close();
    }
  }

  ViewDetails() { }

  DownloadFile(): void {
    this.downloadService.DownloadSingleFile(this.File.FileId);
  }

  RenameFile() { }

  DeleteFile() { }
}
