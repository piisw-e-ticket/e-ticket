import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';

@Injectable({
  providedIn: 'root'
})

export class TicketPeriodicGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {

  }

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const discounted = route.params['discount'];
    const duration = parseInt(route.params['duration']);
    const isDurationCorrect = [1, 3, 6, 12].includes(duration);
    let eligibleForDiscount = false;
    this.authService.getUserInfo().subscribe(userInfo => eligibleForDiscount = userInfo?.eligibleForDiscount!);
    const isPathAllowed = discounted == "true" ? eligibleForDiscount && isDurationCorrect : isDurationCorrect;
    if (isPathAllowed) {
      return true;
    } else {
      this.router.navigateByUrl("/ticket/offer/periodic");
      return false;
    }
  }
  
}
