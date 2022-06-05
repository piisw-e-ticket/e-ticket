import { Component, OnInit } from '@angular/core';
import { UserInfoDto } from 'src/app/features/auth/models/userInfoDto';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-ticket-single',
  templateUrl: './ticket-single.component.html',
  styleUrls: ['./ticket-single.component.css']
})
export class TicketSingleComponent implements OnInit {

  userInfo: UserInfoDto | null = null;

  constructor(private authService: AuthService, private ticketService: TicketService) { 
  }

  ngOnInit(): void {
    this.authService.getUserInfo().subscribe(val => this.userInfo = val);
  }

  onSubmit(discounted: boolean) {
    this.ticketService.buySingleTicket(this.userInfo?.username!, discounted).subscribe();
  }

}
