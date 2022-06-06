import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { ActivatedRoute, Router } from '@angular/router';
import * as moment from 'moment';
import { UserInfoDto } from 'src/app/features/auth/models/userInfoDto';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-ticket-periodic-order',
  templateUrl: './ticket-periodic-order.component.html',
  styleUrls: ['./ticket-periodic-order.component.css']
})
export class TicketPeriodicOrderComponent implements OnInit {

  startDate: Date | null = null;
  endDate: Date | null = null;
  userInfo: UserInfoDto | null = null;

  constructor(private _adapter: DateAdapter<any>,
    @Inject(MAT_DATE_LOCALE) private _locale: string,
    private route: ActivatedRoute,
    private ticketService: TicketService,
    private authService: AuthService,
    private router: Router) {
      this._locale = "pl";
      this._adapter.setLocale(this._locale);
    }

  dateFilter(d: Date | null): boolean {
    const today = new Date();
    return d == null || moment(d).isSameOrAfter(today, 'day');
  }

  ngOnInit(): void {
    this.authService.getUserInfo().subscribe(val => this.userInfo = val);
  }

  changeDate(event: MatDatepickerInputEvent<Date>) {
    this.startDate = event.value;
    const tempDate = new Date(event.value?.getTime()!);
    tempDate.setMonth(tempDate.getMonth() + parseInt(this.route.snapshot.params['duration']))
    this.endDate = tempDate;
  }

  onSubmit() {
    const discounted = this.route.snapshot.params['discount'] == 'true';
    const startDateFormatted = moment(this.startDate).utc().format();
    const endDateFormatted = moment(this.endDate).utc().format();
    this.ticketService.buyPeriodicTicket(this.userInfo?.username!, discounted, startDateFormatted, endDateFormatted)
      .subscribe(_ => this.router.navigateByUrl('/ticket/purchased'));
  }

}
