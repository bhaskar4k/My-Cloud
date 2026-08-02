import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomAlertComponent } from '../../common-components/custom-alert/custom-alert.component';
import { MatDialog } from '@angular/material/dialog';
import { ResponseTypeColor } from '../../constants/commonConsts';

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
    private router: Router
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const folder = params.get('folder')!;
      this.CurrentFolder = folder;
    });

    if (!this.HasAccessToFolder(this.CurrentFolder)) {
      this.dialog.open(CustomAlertComponent, {
        data: { text: "You do not have access to this folder.", type: ResponseTypeColor.ERROR }
      });
      this.router.navigate(['/error']);
    }
  }

  HasAccessToFolder(folder: string): boolean {
    // Implement folder access logic here
    return false; // Placeholder return value
  }
}
