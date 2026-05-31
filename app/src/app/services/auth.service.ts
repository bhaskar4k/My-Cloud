import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Endpoints, GetBaseURL } from '../endpoints/endpoint';
import { Observable } from 'rxjs';
import { ApiResponseDto } from '../models/dto.model';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    constructor(private http: HttpClient) { }

    Register(Payload: any): Observable<ApiResponseDto> {
        return this.http.post<ApiResponseDto>(GetBaseURL() + Endpoints.Auth.Register, Payload);
    }
}
