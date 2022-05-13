import { Injectable } from '@angular/core';
import {
  Router, Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { map, Observable, of } from 'rxjs';
import { UserDto } from '../models/userDto';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserResolver implements Resolve<UserDto> {

  constructor(private authService: AuthService) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<UserDto> {
    return this.authService.getUserInfo().pipe(map(user => user!));
  }
}
