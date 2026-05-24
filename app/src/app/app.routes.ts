import { Routes } from '@angular/router';
import { LayoutBaseComponent } from './page-components/layout/layout-base/layout-base.component';
import { HomeComponent } from './page-components/home/home.component';
import { ErrorComponent } from './page-components/error/error.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutBaseComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },
      { path: '**', component: ErrorComponent }
    ]
  }
];
