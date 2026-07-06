import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Endpoints, GetBaseURL } from '../endpoints/endpoint';
import { Observable } from 'rxjs';
import { ApiResponseDto } from '../models/dto.model';
import { JwtTokenKey } from '../constants/commonConsts';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    constructor(private http: HttpClient) { }

    Register(Payload: any): Observable<ApiResponseDto> {
        return this.http.post<ApiResponseDto>(GetBaseURL() + Endpoints.Auth.Register, Payload);
    }

    Login(Payload: any): Observable<ApiResponseDto> {
        return this.http.post<ApiResponseDto>(GetBaseURL() + Endpoints.Auth.Login, Payload);
    }

    SaveJwtTokenIntoLocalStorage(JwtToken: string): void {
        let Token = this.GetJwtTokenFromLocalStorage();

        if (Token !== null) {
            this.DeleteJwtTokenFromLocalStorage();
        }

        localStorage.setItem(JwtTokenKey, JwtToken);
    }

    GetJwtTokenFromLocalStorage(): string | null {
        return localStorage.getItem(JwtTokenKey);
    }

    DeleteJwtTokenFromLocalStorage(): void {
        localStorage.removeItem(JwtTokenKey);
    }
}
