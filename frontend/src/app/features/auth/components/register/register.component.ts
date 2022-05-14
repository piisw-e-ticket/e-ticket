import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { RegisterDto } from '../../models/registerDto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  constructor(private authService: AuthService) { }

  registerForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
    confirmPassword: new FormControl('', [Validators.required])
  });

  async onSubmit() {
    const registerDto = this.registerForm.value as RegisterDto;
    console.log(registerDto);
    await this.authService.register(registerDto);
  }

}
