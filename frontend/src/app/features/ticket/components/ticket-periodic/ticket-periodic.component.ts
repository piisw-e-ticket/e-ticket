import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserInfoDto } from 'src/app/features/auth/models/userInfoDto';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-ticket-periodic',
  templateUrl: './ticket-periodic.component.html',
  styleUrls: ['./ticket-periodic.component.css']
})
export class TicketPeriodicComponent implements OnInit {

  userInfo: UserInfoDto | null = null;

  constructor(private authService: AuthService, private ticketService: TicketService, private router: Router) { 
  }

  ngOnInit(): void {
    this.authService.getUserInfo().subscribe(val => this.userInfo = val);
  }
}
