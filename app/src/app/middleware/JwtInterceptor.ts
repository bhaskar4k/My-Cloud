import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs';
import { throwError } from 'rxjs';
import { JwtTokenKey } from '../constants/commonConsts';
import { AuthService } from '../services/auth.service';

export const JwtInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const authService = inject(AuthService);

  const token = localStorage.getItem(JwtTokenKey);

  const authReq = token
    ? req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    })
    : req;

  return next(authReq).pipe(
    catchError((error) => {

      if (error.status === 401) {
        authService.DeleteJwtTokenFromLocalStorage();
        if (router.url !== '/login') {
          router.navigate(['/login']);
        }
      }

      return throwError(() => error);
    })
  );
};