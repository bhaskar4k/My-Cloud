import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenuService } from '../../../services/menu.service';
import { MenuItem } from '../../../models/menu.model';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ApiResponseDto } from '../../../models/dto.model';
import { MatDialog } from '@angular/material/dialog';
import { ResponseTypeColor } from '../../../constants/commonConsts';
import { CustomAlertComponent } from '../../../common-components/custom-alert/custom-alert.component';

@Component({
  selector: 'app-side-panel',
  imports: [
    CommonModule,
    RouterModule,
    MatTooltipModule
  ],
  templateUrl: './side-panel.component.html',
  styleUrl: './side-panel.component.css'
})
export class SidePanelComponent implements OnInit {
  @Input() isOpen = true;

  HasFetchedMenuData: boolean = false;
  AllMenuItems: MenuItem[] = [];
  expandedMenus: Set<number> = new Set();


  constructor(
    private menuService: MenuService,
    private dialog: MatDialog
  ) { }

  ngOnInit() {
    this.HasFetchedMenuData = false;

    this.menuService.GetMenu().subscribe({
      next: (response: ApiResponseDto) => {
        if (response.success !== true || response.statusCode !== 200) {
          this.dialog.open(CustomAlertComponent, { data: { text: response.message, type: ResponseTypeColor.ERROR } });
          return;
        }

        this.AllMenuItems = response.data;
        this.HasFetchedMenuData = true;
      },
      error: (err: any) => {
        this.dialog.open(CustomAlertComponent, { data: { text: "Failed to fetch menu list.", type: ResponseTypeColor.ERROR } });
      }
    });
  }

  onToggleSidePanel() {
    this.isOpen = !this.isOpen;
  }

  toggleSubmenu(menuId: number) {
    if (this.expandedMenus.has(menuId)) {
      this.expandedMenus.delete(menuId);
    } else {
      this.expandedMenus.add(menuId);
    }
  }

  isMenuExpanded(menuId: number): boolean {
    return this.expandedMenus.has(menuId);
  }

  hasSubmenu(item: MenuItem): boolean {
    return item.submenu && item.submenu.length > 0;
  }
}
