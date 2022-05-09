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
}
