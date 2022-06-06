import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { LoginDto } from '../../models/loginDto';
import { Router } from '@angular/router';
import { catchError, mergeMap, of } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(private authService: AuthService, private router: Router) { }

  loginForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
  });

  error: String | null = null;
  
  get username() { return this.loginForm.get("username") }
  get password() { return this.loginForm.get("password") }

  onSubmit() {
    const loginDto = this.loginForm.value as LoginDto;
    let role: string | undefined = '';
    this.authService.login(loginDto)
      .pipe(
        catchError(error => of(error)),
        mergeMap(res => {
          if (res instanceof HttpErrorResponse) {
            this.error = res.error;
          }
          return this.authService.getUserInfo();
        })
      ).subscribe(res => {
        role = res?.role;
        if (role === 'TICKET_COLLECTOR') {
          this.router.navigateByUrl('/verify');
        }
        else this.router.navigateByUrl('/ticket/offer');
      });
  }
}
