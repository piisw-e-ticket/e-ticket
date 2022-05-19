import { HttpClient, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { mergeMap, Observable } from "rxjs";
import { AuthService } from "../services/auth.service";

@Injectable()
export class SessionKeeperInterceptor implements HttpInterceptor {

  constructor(private http: HttpClient, private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log("x");
    if (this.authService.isLoggedIn() || req.url.includes("/auth/refresh"))
      return next.handle(req);

    return this.authService.refresh().pipe(mergeMap(_ => next.handle(req)));
  }
}
