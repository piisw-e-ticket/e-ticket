import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import * as moment from 'moment';
import { UserInfoDto } from 'src/app/features/auth/models/userInfoDto';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-ticket-time-limited',
  templateUrl: './ticket-time-limited.component.html',
  styleUrls: ['./ticket-time-limited.component.css']
})
export class TicketTimeLimitedComponent implements OnInit {

  userInfo: UserInfoDto | null = null;

  constructor(private authService: AuthService, private ticketService: TicketService, private router: Router) { 
  }

  ngOnInit(): void {
    this.authService.getUserInfo().subscribe(val => this.userInfo = val);
  }

  onSubmit(discounted: boolean, duration: number, durationUnit: string) {
    const startDateFormatted = moment().utc().format();
    const durationUnitVerified = durationUnit === 'days' ? 'days' : durationUnit === 'hours' ? 'hours' : 'minutes';
    const endDateFormatted = moment().add(duration, durationUnitVerified).utc().format();
    this.ticketService.buyPeriodicTicket(this.userInfo?.username!, discounted, startDateFormatted, endDateFormatted)
      .subscribe(_ => this.router.navigateByUrl('/ticket/purchased'));
  }
}
