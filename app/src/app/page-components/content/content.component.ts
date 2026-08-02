import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomAlertComponent } from '../../common-components/custom-alert/custom-alert.component';
import { MatDialog } from '@angular/material/dialog';
import { ResponseTypeColor } from '../../constants/commonConsts';
import { FolderService } from '../../services/folder.service';
import { ApiResponseDto } from '../../models/dto.model';
import { catchError, map, Observable, of } from 'rxjs';

@Component({
  selector: 'app-content',
  imports: [],
  templateUrl: './content.component.html',
  styleUrl: './content.component.css'
})
export class ContentComponent {
  CurrentFolder: string = 'root';
  constructor(
    private route: ActivatedRoute,
    private dialog: MatDialog,
    private router: Router,
    private folderService: FolderService
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const folder = params.get('folder')!;
      this.CurrentFolder = folder;
    });

    this.HasAccessToFolder().subscribe(hasAccess => {
      if (!hasAccess) this.router.navigate(['/error']);
    });
  }

  HasAccessToFolder(): Observable<boolean> {
    return this.folderService.ValidateFolderAccess(this.CurrentFolder).pipe(
      map((response: ApiResponseDto) => {
        if (response.success && response.statusCode === 200) {
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
}
