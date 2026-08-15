import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomAlertComponent } from '../../page-components-shared/custom-alert/custom-alert.component';
import { MatDialog } from '@angular/material/dialog';
import { ResponseTypeColor } from '../../constants/commonConsts';
import { FolderService } from '../../services/folder.service';
import { ApiResponseDto } from '../../models/dto.model';
import { catchError, map, Observable, of } from 'rxjs';
import { FolderDetailsEntity, FolderInfoEntity } from '../../models/folder.model';
import { FileDetailsEntity } from '../../models/file.model';
import { CommonModule } from '@angular/common';
import { FileContentComponent } from '../content/file-content/file-content.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FileService } from '../../services/file.service';
import { FolderRoutingPage } from '../../enums/common.enum';


@Component({
  selector: 'app-favourite',
  imports: [
    CommonModule,
    FileContentComponent,
    MatProgressSpinnerModule
  ],
  templateUrl: './favourite.component.html',
  styleUrl: './favourite.component.css'
})
export class FavouriteComponent {
  CurrentFolderId: string = 'root';
  FullFolderPath: FolderInfoEntity[] = [];

  AllFolder: FolderDetailsEntity = { HasFolder: false, FolderCount: 0, FoldersList: [] };
  RenderFolderList: boolean = true;

  AllFile: FileDetailsEntity = { HasFile: false, FileCount: 0, FilesList: [] };
  RenderFileList: boolean = true;

  MatProgressBar = false;
  MatProgressBar1 = false;

  FolderRoutingPage = FolderRoutingPage;

  constructor(
    private route: ActivatedRoute,
    private dialog: MatDialog,
    private router: Router,
    private folderService: FolderService,
    private fileService: FileService
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const folder = params.get('folder')!;
      this.CurrentFolderId = folder;
    });

    this.HasAccessToFolder().subscribe(hasAccess => {
      if (!hasAccess) {
        this.router.navigate(['/error']);
      } else {
        this.GetAllChildFiles();
      }
    });
  }

  HasAccessToFolder(): Observable<boolean> {
    this.MatProgressBar = true;

    return this.folderService.ValidateFolderAccess(this.CurrentFolderId).pipe(
      map((response: ApiResponseDto) => {
        this.MatProgressBar = false;

        if (response.success && response.statusCode === 200) {
          this.FullFolderPath = response.data || [] as FolderInfoEntity[];

          if (this.FullFolderPath.length === 0 || this.FullFolderPath[this.FullFolderPath.length - 1].FolderId !== this.CurrentFolderId) {
            this.dialog.open(CustomAlertComponent, { data: { text: "Failed to validate folder access.", type: ResponseTypeColor.ERROR } });
            return false;
          }

          return true;
        }

        this.dialog.open(CustomAlertComponent, { data: { text: response.message, type: ResponseTypeColor.ERROR } });
        return false;
      }),
      catchError((ex) => {
        this.MatProgressBar = false;
        this.dialog.open(CustomAlertComponent, { data: { text: "Failed to validate folder access.", type: ResponseTypeColor.ERROR } });
        return of(false);
      })
    );
  }

  GetAllChildFiles() {
    this.RenderFileList = false;
    this.MatProgressBar1 = true;

    this.fileService.GetAllFavouriteFiles().subscribe({
      next: (response: ApiResponseDto) => {
        this.MatProgressBar1 = false;

        if (response.success === false || response.statusCode !== 200) {
          this.dialog.open(CustomAlertComponent, { data: { text: response.message, type: ResponseTypeColor.ERROR } });
        }

        this.AllFile = response.data as FileDetailsEntity;
        this.AllFile.FilesList.reverse();
        this.RenderFileList = true;
      },
      error: (err: any) => {
        this.dialog.open(CustomAlertComponent, { data: { text: "Failed to fetch all file lists.", type: ResponseTypeColor.ERROR } });
        this.MatProgressBar1 = false;
      }
    });
  }

  NavigateToFolder(Folder: FolderInfoEntity) {
    console.log(Folder);
    window.location.href = "/" + this.FolderRoutingPage.Favourite + "/" + Folder.FolderId;
  }

  IsMatProgressBarVisible(): boolean {
    return this.MatProgressBar || this.MatProgressBar1;
  }
}
