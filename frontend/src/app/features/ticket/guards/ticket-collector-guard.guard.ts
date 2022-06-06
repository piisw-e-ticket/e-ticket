import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { UserInfoDto } from '../../auth/models/userInfoDto';
import { AuthService } from '../../auth/services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class TicketCollectorGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {

  }

  canActivate(): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return new Observable<boolean>(obs => {
      let role = '';
      this.authService.getUserInfo().subscribe(userInfo => {
        role = userInfo?.role!;
        if (role === 'TICKET_COLLECTOR') {
          obs.next(true);
        } else {
          this.router.navigateByUrl("/ticket/offer");
          obs.next(false);
        }
      });
    });
  }
  
}
