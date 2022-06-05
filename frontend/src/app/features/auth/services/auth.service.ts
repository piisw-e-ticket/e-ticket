import { Injectable } from '@angular/core';
import { Observable, of, Subject, Subscriber } from 'rxjs';
import { LoginDto } from '../models/loginDto';
import { RegisterDto } from '../models/registerDto';
import { UserDto } from '../models/userDto';
import { HttpClient } from '@angular/common/http';
import * as moment from "moment";
import { catchError, map, shareReplay, tap } from 'rxjs/operators';
import { AuthResultDto } from '../models/authResultDto';
import { UserInfoDto } from '../models/userInfoDto';
import jwtDecode from 'jwt-decode';
import { JwtDto } from '../models/jwtDto';

interface SessionKeys {
  refreshToken: string,
  sessionExpirationDate: string
}

class Session {
  static instance() {
    return new Session({
      refreshToken: "23fd",
      sessionExpirationDate: "d3nt"
    });
  }

  private keys: SessionKeys

  private constructor(keys: SessionKeys) {
    this.keys = keys
  }

  exists(): boolean {
    return this.getSessionExpirationDate() != null;
  }

  isExpired(): boolean {
    return this.exists()
      ? moment().isAfter(this.getSessionExpirationDate())
      : true;
  }

  set(refreshToken: string, sessionExpiresAt: string): void {
    localStorage.setItem(this.keys.refreshToken, refreshToken);
    localStorage.setItem(this.keys.sessionExpirationDate, sessionExpiresAt);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.keys.refreshToken);
  }

  getSessionExpirationDate(): string | null {
    return localStorage.getItem(this.keys.sessionExpirationDate);
  }

  close(): void {
    localStorage.removeItem(this.keys.refreshToken);
    localStorage.removeItem(this.keys.sessionExpirationDate);
  }
}


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private session = Session.instance();

  constructor(private http: HttpClient) {}

  private setSession(authResult: AuthResultDto) {
    this.session.set(authResult.refreshToken, authResult.accessTokenExpirationDate);
  }

  isLoggedIn(): boolean {
    return !this.session.isExpired()
  }

  getUserInfo(): Observable<UserInfoDto | null> {
    return this.http.get<UserInfoDto>("/auth/info", {headers: {username: (jwtDecode(this.session.getRefreshToken()!) as JwtDto).sub}});
  }

  refresh(): Observable<boolean> {
    if (!this.session.exists())
      return of(false); // cannot resfresh not existing session

    const headers = { headers: { "Authorization": `bearer ${this.session.getRefreshToken()}` } }
    return this.http.post<AuthResultDto>("/auth/refresh?setCookie=true", "", headers).pipe(
      map(authResult => {this.setSession(authResult); return true;}),
      catchError((e, _) => {this.logout(); return of(false);})
    );
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
    this.session.close();
  }
}
