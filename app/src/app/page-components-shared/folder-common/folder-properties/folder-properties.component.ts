import { Component, ElementRef, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { FolderDeleteEmitEntity, FolderInfoEntity } from '../../../models/folder.model';
import { DownloadService } from '../../../services/download.service';
import { FolderMenuStateService } from '../../../services/folder-menu-state.service';
import { MatDialog } from '@angular/material/dialog';
import { FolderService } from '../../../services/folder.service';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { AnimationEvent } from '@angular/animations';
import { FolderDetailsComponent } from '../folder-details/folder-details.component';

@Component({
  selector: 'app-folder-properties',
  imports: [
    CommonModule
  ],
  templateUrl: './folder-properties.component.html',
  styleUrl: './folder-properties.component.css',
  animations: [
    trigger('popMenu', [

      state('open', style({
        opacity: 1,
        transform: 'scale(1) translateY(0)',
        visibility: 'visible'
      })),

      state('closed', style({
        opacity: 0,
        transform: 'scale(0.92) translateY(-6px)',
        visibility: 'hidden'
      })),

      transition('closed => open', [
        animate('260ms cubic-bezier(0.2, 0.9, 0.3, 1.2)')
      ]),

      transition('open => closed', [
        animate('220ms cubic-bezier(0.4, 0, 0.2, 1)')
      ])

    ])
  ]
})
export class FolderPropertiesComponent {
  @Input() ShowMore = false;

  @Input() FolderInfo: FolderInfoEntity = {
    FolderId: '',
    FolderName: '',
    Depth: 0,
    SubFolderCount: 0,
    FilesCount: 0,
    Favourite: false,
    CreatedAt: '',
    CreatedAgo: '',
    ModifiedAt: '',
    Deleted: false,
    DeletedAt: '',
    AutoDeletingAt: ''
  };

  Folder: FolderInfoEntity = {
    FolderId: '',
    FolderName: '',
    Depth: 0,
    SubFolderCount: 0,
    FilesCount: 0,
    Favourite: false,
    CreatedAt: '',
    CreatedAgo: '',
    ModifiedAt: '',
    Deleted: false,
    DeletedAt: '',
    AutoDeletingAt: ''
  };

  @Output() UpdatedFolder = new EventEmitter<FolderInfoEntity>();
  @Output() DeletedFolder = new EventEmitter<FolderDeleteEmitEntity>();

  MatProgressBar: boolean = false;
  IsFullyClosed = true;

  constructor(
    private downloadService: DownloadService,
    private elementRef: ElementRef,
    private menuState: FolderMenuStateService,
    private dialog: MatDialog,
    private folderService: FolderService,
  ) { }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.menuState.close();
    }
  }

  OnAnimStart(event: AnimationEvent) {
    // As soon as any transition begins, make sure the element is visible/animatable
    this.IsFullyClosed = false;
  }

  OnAnimDone(event: AnimationEvent) {
    // Only hide it from layout once it has fully finished closing
    this.IsFullyClosed = event.toState === 'closed';
  }

  ngOnInit(): void {
    this.Folder = this.FolderInfo;
  }

  ViewDetails() {
    this.dialog.open(FolderDetailsComponent, {
      width: '60rem',
      data: this.Folder
    });
  }

  DownloadFile() {

  }

  FavouriteFile() {

  }

  RenameFile() {

  }

  DeleteFile() {

  }
}
