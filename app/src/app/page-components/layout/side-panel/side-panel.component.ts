import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenuService } from '../../../services/menu.service';
import { MenuItem } from '../../../models/menu.model';

@Component({
  selector: 'app-side-panel',
  imports: [CommonModule, RouterModule],
  templateUrl: './side-panel.component.html',
  styleUrl: './side-panel.component.css'
})
export class SidePanelComponent implements OnInit {
  @Input() isOpen = true;

  menuItems: MenuItem[] = [];
  expandedMenus: Set<number> = new Set();

  constructor(private menuService: MenuService) { }

  ngOnInit() {
    this.menuItems = this.menuService.getMenu();
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
