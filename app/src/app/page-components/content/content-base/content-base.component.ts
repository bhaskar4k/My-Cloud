import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomAlertComponent } from '../../../common-components/custom-alert/custom-alert.component';
import { MatDialog } from '@angular/material/dialog';
import { ResponseTypeColor } from '../../../constants/commonConsts';
import { FolderService } from '../../../services/folder.service';
import { ApiResponseDto } from '../../../models/dto.model';
import { catchError, map, Observable, of } from 'rxjs';
import { FolderDetailsEntity, FolderInfoEntity } from '../../../models/folder.model';
import { CommonModule } from '@angular/common';
import { CreateFolderComponent } from '../../../common-components/create-folder/create-folder.component';
import { UploadComponent } from '../../../common-components/upload/upload.component';

@Component({
  selector: 'app-content-base',
  imports: [
    CommonModule
  ],
  templateUrl: './content-base.component.html',
  styleUrl: './content-base.component.css'
})
export class ContentBaseComponent {
  CurrentFolderId: string = 'root';
  FullFolderPath: FolderInfoEntity[] = [];
  AllFolder: FolderDetailsEntity = { HasFolder: false, FolderCount: 0, FoldersList: [] };

  MatProgressBar = false;

  constructor(
    private route: ActivatedRoute,
    private dialog: MatDialog,
    private router: Router,
    private folderService: FolderService
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
        this.GetAllChildFolders();
      }
    });
  }

  HasAccessToFolder(): Observable<boolean> {
    return this.folderService.ValidateFolderAccess(this.CurrentFolderId).pipe(
      map((response: ApiResponseDto) => {
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
        console.log(ex)
        this.dialog.open(CustomAlertComponent, { data: { text: "Failed to validate folder access.", type: ResponseTypeColor.ERROR } });
        return of(false);
      })
    );
  }

  CreateFolder() {
    const dialogRef = this.dialog.open(CreateFolderComponent, {
      width: '30rem',
      disableClose: true
    });

    dialogRef.afterClosed().subscribe(folderName => {
      if (folderName) {
        let CreateFolderPayload: FolderInfoEntity = {
          FolderId: this.CurrentFolderId,
          FolderName: folderName,
        }

        this.MatProgressBar = true;

        this.folderService.CreateFolder(CreateFolderPayload).subscribe({
          next: (response: ApiResponseDto) => {
            this.MatProgressBar = false;

            if (response.success === true && response.statusCode === 200) {
              this.dialog.open(CustomAlertComponent, { data: { text: response.message, type: ResponseTypeColor.SUCCESS } });
              this.AllFolder.FolderCount += 1;
              this.AllFolder.HasFolder = true;
              this.AllFolder.FoldersList.reverse();
              this.AllFolder.FoldersList.push(response.data as FolderInfoEntity);
              this.AllFolder.FoldersList.reverse();
            } else {
              this.dialog.open(CustomAlertComponent, { data: { text: response.message, type: ResponseTypeColor.ERROR } });
            }
          },
          error: (err: any) => {
            this.dialog.open(CustomAlertComponent, { data: { text: "Failed to create folder.", type: ResponseTypeColor.ERROR } });
            this.MatProgressBar = false;
          }
        });
      }
    });
  }

  GetAllChildFolders() {
    this.folderService.GetAllChildFoldersByFolderId(this.CurrentFolderId).subscribe({
      next: (response: ApiResponseDto) => {
        this.MatProgressBar = false;

        if (response.success === false || response.statusCode !== 200) {
          this.dialog.open(CustomAlertComponent, { data: { text: response.message, type: ResponseTypeColor.ERROR } });
        }

        this.AllFolder = response.data as FolderDetailsEntity;
        this.AllFolder.FoldersList.reverse();
      },
      error: (err: any) => {
        this.dialog.open(CustomAlertComponent, { data: { text: "Failed to fetch all folder lists.", type: ResponseTypeColor.ERROR } });
        this.MatProgressBar = false;
      }
    });
  }

  UploadFile() {
    const dialogRef = this.dialog.open(UploadComponent, {
      data: { FolderId: this.CurrentFolderId },
      disableClose: true
    });
  }

  NavigateToFolder(Folder: FolderInfoEntity) {
    window.location.href = "/content/" + Folder.FolderId;
  }

  ViewMoreInFolder(Folder: FolderInfoEntity) {

  }
}
