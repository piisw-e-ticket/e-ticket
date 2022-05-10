import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  title = 'frontend';

  constructor(private readonly activatedRoute: ActivatedRoute) {
    console.log(activatedRoute);
  }

  register() {
    fetch('/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({username: 'rafal', password: 'pass', email: 'rafal@example.com'}),
    });
  }

  login() {
    fetch('/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({username: 'rafal', password: 'pass'}),
    });
  }
}
