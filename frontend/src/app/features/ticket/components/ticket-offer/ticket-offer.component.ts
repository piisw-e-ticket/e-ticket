import { Component, OnInit } from '@angular/core';
import { UserInfoDto } from 'src/app/features/auth/models/userInfoDto';
import { AuthService } from 'src/app/features/auth/services/auth.service';

@Component({
  selector: 'app-ticket-offer',
  templateUrl: './ticket-offer.component.html',
  styleUrls: ['./ticket-offer.component.css']
})
export class TicketOfferComponent implements OnInit {

  userInfo: UserInfoDto | null = null;

  constructor(private authService: AuthService) { 
  }

  ngOnInit(): void {
    this.authService.getUserInfo().subscribe(val => this.userInfo = val);
  }

}
