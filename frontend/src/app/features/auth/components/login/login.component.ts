import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { LoginDto } from '../../models/loginDto';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  constructor(private authService: AuthService) { }

  loginForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
  });

  async onSubmit() {
    const loginDto = this.loginForm.value as LoginDto;
    console.log(loginDto);
    await this.authService.login(loginDto);
  }

}
