import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TicketPeriodicDto } from '../models/ticketPeriodicDto';
import { TicketSingleDto } from '../models/ticketSingleDto';

@Injectable({
  providedIn: 'root'
})
export class TicketService {


  constructor(private http: HttpClient) {
  }

  buySingleTicket(username: string, discounted: boolean): Observable<TicketSingleDto> {
    return this.http.post<TicketSingleDto>(`/tickets/single?discounted=${discounted}`, null, {headers: {username}});
  }

  buyPeriodicTicket(username: string, discounted: boolean, startDate: string, endDate: string): Observable<TicketPeriodicDto> {
    return this.http.post<TicketPeriodicDto>(`/tickets/periodic?discounted=${discounted}`, {startDate, endDate}, {headers: {username}});
  }
}
