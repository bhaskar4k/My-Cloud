import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomAlertComponent } from '../../common-components/custom-alert/custom-alert.component';
import { MatDialog } from '@angular/material/dialog';
import { ResponseTypeColor } from '../../constants/commonConsts';
import { FolderService } from '../../services/folder.service';
import { ApiResponseDto } from '../../models/dto.model';
import { catchError, map, Observable, of } from 'rxjs';
import { FolderInfoEntity } from '../../models/folder.model';
import { CommonModule } from '@angular/common';
import { CreateFolderComponent } from '../create-folder/create-folder.component';

@Component({
  selector: 'app-content',
  imports: [
    CommonModule
  ],
  templateUrl: './content.component.html',
  styleUrl: './content.component.css'
})
export class ContentComponent {
  CurrentFolderId: string = 'root';
  FullFolderPath: FolderInfoEntity[] = [];

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
      if (!hasAccess) this.router.navigate(['/error']);
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
      catchError(() => {
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
          FolderName: folderName
        }

        this.MatProgressBar = true;

        this.folderService.CreateFolder(CreateFolderPayload).subscribe({
          next: (response: ApiResponseDto) => {
            this.MatProgressBar = false;

            if (response.success === true && response.statusCode === 200) {
              this.dialog.open(CustomAlertComponent, { data: { text: response.message, type: ResponseTypeColor.SUCCESS } });
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
}
