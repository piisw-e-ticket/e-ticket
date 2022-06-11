import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import * as moment from 'moment';
import { mergeMap } from 'rxjs';
import { UserInfoDto } from 'src/app/features/auth/models/userInfoDto';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { TicketsBoughtDto } from '../../models/ticketsBoughtDto';
import { TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  userInfo: UserInfoDto | null = null;
  ticketsBought: TicketsBoughtDto | null = null;
  ticketsType: string = 'single';

  constructor(private ticketService: TicketService, private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.authService.getUserInfo().pipe(
      mergeMap(val => {
        this.userInfo = val;
        return this.ticketService.getUserTickets(val?.username!)
      })
    ).subscribe(val => this.ticketsBought = val);
  }

  formatDate(startDate: string, endDate: string): string {    
    return `${moment(startDate).format('DD.MM.YYYY, HH:mm')} – ${moment(endDate).format('DD.MM.YYYY, HH:mm')}`
  }

  checkValidity(startDate: string, endDate: string): boolean {
    return moment(moment()).isBetween(startDate, endDate);
  }
}
