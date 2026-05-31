import { Routes } from '@angular/router';
import { LayoutBaseComponent } from './page-components/layout/layout-base/layout-base.component';
import { HomeComponent } from './page-components/home/home.component';
import { ErrorComponent } from './page-components/error/error.component';
import { RegisterComponent } from './page-components/register/register.component';
import { LoginComponent } from './page-components/login/login.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutBaseComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: HomeComponent },

      { path: 'register', component: RegisterComponent },
      { path: 'login', component: LoginComponent },

      { path: '**', component: ErrorComponent },
    ]
  }
];
