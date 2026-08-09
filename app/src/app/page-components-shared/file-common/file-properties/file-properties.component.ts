import { Component, ElementRef, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { FileInfoEntity, FileRenameInputEntity } from '../../../models/folder.model';
import { DownloadService } from '../../../services/download.service';
import { FileMenuStateService } from '../../../services/file-menu-state.service';
import { trigger, transition, style, animate } from '@angular/animations';
import { MatDialog } from '@angular/material/dialog';
import { FileDetailsComponent } from '../file-details/file-details.component';
import { RenameFileComponent } from '../rename-file/rename-file.component';
import { FileService } from '../../../services/file.service';
import { ApiResponseDto } from '../../../models/dto.model';
import { CustomAlertComponent } from '../../custom-alert/custom-alert.component';
import { ResponseTypeColor } from '../../../constants/commonConsts';

@Component({
  selector: 'app-file-properties',
  imports: [],
  templateUrl: './file-properties.component.html',
  styleUrl: './file-properties.component.css',
  host: {
    '[@popMenu]': ''
  },
  animations: [
    trigger('popMenu', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.92) translateY(-6px)' }),
        animate('260ms cubic-bezier(0.2, 0.9, 0.3, 1.2)',
          style({ opacity: 1, transform: 'scale(1) translateY(0)' }))
      ]),
      transition(':leave', [
        animate('220ms cubic-bezier(0.4, 0, 0.2, 1)',
          style({ opacity: 0, transform: 'scale(0.92) translateY(-6px)' }))
      ])
    ])
  ]
})
export class FilePropertiesComponent {
  @Input() FileInfo: FileInfoEntity = {
    FileId: '',
    OriginalName: '',
    FileExtension: '',
    ContentType: '',
    FileSize: 0,
    CreatedAt: '',
    UploadedAgo: '',
    ModifiedAt: ''
  };

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

  @Output() UpdatedFile = new EventEmitter<FileInfoEntity>();

  MatProgressBar: boolean = false;

  constructor(
    private downloadService: DownloadService,
    private elementRef: ElementRef,
    private menuState: FileMenuStateService,
    private dialog: MatDialog,
    private fileService: FileService,
  ) { }

  ngOnInit(): void {
    this.File = this.FileInfo;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.menuState.close();
    }
  }

  ViewDetails() {
    this.dialog.open(FileDetailsComponent, {
      width: '60rem',
      data: this.File
    });
  }

  DownloadFile(): void {
    this.downloadService.DownloadSingleFile(this.File.FileId);
  }

  FavouriteFile() { }

  RenameFile() {
    const dialogRef = this.dialog.open(RenameFileComponent, {
      data: this.File,
      width: '30rem',
      disableClose: true
    });

    dialogRef.afterClosed().subscribe((UpdatedFile: FileInfoEntity) => {
      if (UpdatedFile) {
        this.FileInfo = UpdatedFile;
        this.File = UpdatedFile;
        this.UpdatedFile.emit(UpdatedFile);
      }

      this.menuState.close();
    });
  }

  DeleteFile() { }
}
