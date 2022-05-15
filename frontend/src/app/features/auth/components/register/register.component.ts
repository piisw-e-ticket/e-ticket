import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { Router } from '@angular/router';
import { catchError, of } from 'rxjs';
import { RegisterDto } from '../../models/registerDto';
import { AuthService } from '../../services/auth.service';

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null): boolean {
    const invalidCtrl = !!(control && control.invalid && control.parent?.dirty);
    const invalidParent = !!(control && control.parent && control.parent.invalid && control.parent.dirty);

    return (invalidCtrl || invalidParent);
  }
}

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  constructor(private authService: AuthService, private router: Router) { }

  matcher = new MyErrorStateMatcher();

  checkPasswords: ValidatorFn = (group: AbstractControl):  ValidationErrors | null => { 
    let pass = group.get('password')?.value;
    let confirmPass = group.get('confirmPassword')?.value
    return pass === confirmPass ? null : { notSame: true }
  }

  registerForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    fullPassword: new FormGroup({
      password: new FormControl('', [Validators.required]),
      confirmPassword: new FormControl('', [Validators.required])
    }, {
      validators: this.checkPasswords
    })
  });

  get username() { return this.registerForm.get("username") }
  get email() { return this.registerForm.get("email") }
  get fullPassword() { return this.registerForm.get("fullPassword") }
  get password() { return this.registerForm.get("fullPassword.password") }
  get confirmPassword() { return this.registerForm.get("fullPassword.confirmPassword") }

  error: String | null = null;

  onSubmit() {
    const registerDto = {
      username: this.registerForm.get("username")!.value,
      email: this.registerForm.get("email")!.value,
      password: this.registerForm.get("fullPassword.password")!.value
    } as RegisterDto;
    this.authService.register(registerDto)
      .pipe(catchError(error => of(error)))
      .subscribe(res => {
        if (res instanceof HttpErrorResponse) {
          this.error = res.error;
        }
        this.router.navigateByUrl('/aut/profile')
      });
  }
}
