import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { LoginDto } from '../models/loginDto';
import { RegisterDto } from '../models/registerDto';
import { UserDto } from '../models/userDto';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private user: UserDto | null | undefined;

  constructor(private router: Router) {

  }

  login(loginDto: LoginDto) {
    this.user = loginDto as UserDto;
    return this.router.navigate(["/auth/profile"]);
  }

  register(registerDto: RegisterDto) {
    this.user = registerDto as UserDto;
    return this.router.navigate(["/auth/profile"]);
  }

  logout() {
    this.user = null;
    return this.router.navigate(["/auth/login"]);
  }

  getUserInfo(): Observable<UserDto | null> {
    if (this.user !== undefined) return of(this.user);
    return of(null);
  }
}
