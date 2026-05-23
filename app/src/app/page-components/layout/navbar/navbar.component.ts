import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  @Output() toggleSidePanel = new EventEmitter<void>();

  onToggleSidePanel() {
    this.toggleSidePanel.emit();
  }
}
