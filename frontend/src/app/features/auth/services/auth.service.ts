import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { LoginDto } from '../models/loginDto';
import { RegisterDto } from '../models/registerDto';
import { UserDto } from '../models/userDto';
import { HttpClient } from '@angular/common/http';
import * as moment from "moment";
import { shareReplay, tap } from 'rxjs/operators';
import { AuthResultDto } from '../models/authResultDto';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {

  }

  private setSession(authResult: AuthResultDto) {
    localStorage.setItem('refreshToken', authResult.refreshToken);
    localStorage.setItem('expiresAt', authResult.accessTokenExpirationDate);
  }

  private isExpired(): boolean {
    const expiration = localStorage.getItem("expiresAt");
    return expiration ? moment().isAfter(expiration) : true;
  }

  isLoggedIn(): boolean {
    return !this.isExpired();
  }

  login(loginDto: LoginDto): Observable<AuthResultDto> {
    return this.http.post<AuthResultDto>("/auth/login?setCookie=true", loginDto)
      .pipe(
        tap(res => this.setSession(res)),
        shareReplay()
      );
  }

  register(registerDto: RegisterDto): Observable<AuthResultDto> {
    return this.http.post<AuthResultDto>("/auth/register?setCookie=true", registerDto)
      .pipe(
        tap(res => this.setSession(res)),
        shareReplay()
      )
  }

  logout(): void {
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("expiresAt");
  }

  // getUserInfo(): Observable<UserDto | null> {
  //   if (this.user !== undefined) return of(this.user);
  //   return of(null);
  // }
}
