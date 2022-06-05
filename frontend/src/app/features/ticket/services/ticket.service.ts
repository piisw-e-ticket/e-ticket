import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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
}
