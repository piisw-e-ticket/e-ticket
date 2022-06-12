import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TicketsBoughtDto } from '../models/ticketsBoughtDto';
import { TicketSingleDto } from '../models/ticketSingleDto';
import { TicketPeriodicDto } from '../models/ticketPeriodicDto';
import { TicketValidatedDto } from '../models/ticketValidatedDto';
import { ValidationInputsDto } from '../models/validationInputsDto';

import { TicketService } from './ticket.service';

describe('TicketService', () => {
  let service: TicketService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ 
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(TicketService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  })

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get tickets of specified user', () => {
    const tickets: TicketsBoughtDto = {
      singleTickets: [
        {
          id: 1,
          passengerUsername: 'passenger',
          courseId: null,
          isDiscounted: false,
          isPunched: false
        },
        {
          id: 3,
          passengerUsername: 'passenger',
          courseId: 10,
          isDiscounted: false,
          isPunched: true
        }
      ],
      periodicTickets: [
        {
          id: 2,
          passengerUsername: 'passenger',
          startDate: '2022-06-11T17:08:49.414Z',
          endDate: '2022-06-11T18:08:49.414Z',
          isDiscounted: false
        },
      ]
    };

    service.getUserTickets('passenger').subscribe(ticketsRetrieved => {
      expect(ticketsRetrieved.singleTickets.length).toBe(2);
      expect(ticketsRetrieved.periodicTickets.length).toBe(1);
      expect(ticketsRetrieved).toEqual(tickets);
    });

    const request = httpMock.expectOne('/tickets');

    expect(request.request.method).toBe('GET');

    request.flush(tickets);
  });

  it('should post single ticket for specified user', () => {
    const ticket: TicketSingleDto = {
          id: 1,
          passengerUsername: 'passenger',
          courseId: null,
          isDiscounted: true,
          isPunched: false
    };

    service.buySingleTicket('passenger', true).subscribe(boughtTicket => {
      expect(boughtTicket).toBe(ticket);
    });

    const request = httpMock.expectOne(`/tickets/single?discounted=true`);

    expect(request.request.method).toBe('POST');
    expect(request.request.headers.has('username')).toBe(true);
    expect(request.request.headers.get('username')).toBe('passenger');

    request.flush(ticket);
  });

  it('should post periodic ticket for specified user', () => {
    const ticket: TicketPeriodicDto = {
        id: 2,
        passengerUsername: 'passenger',
        startDate: '2022-06-11T17:08:49.414Z',
        endDate: '2022-06-11T18:08:49.414Z',
        isDiscounted: false
    };

    service.buyPeriodicTicket('passenger', true, ticket.startDate, ticket.endDate).subscribe(boughtTicket => {
      expect(boughtTicket).toBe(ticket);
    });

    const request = httpMock.expectOne(`/tickets/periodic?discounted=true`);

    expect(request.request.method).toBe('POST');

    expect(request.request.body).toEqual({startDate: ticket.startDate, endDate: ticket.endDate});
    expect(request.request.headers.has('username')).toBe(true);
    expect(request.request.headers.get('username')).toBe('passenger');

    request.flush(ticket);
  });

  it('should post proper validation request', () => {
    const ticket: TicketValidatedDto = {
      errors: [],
      isValid: true
    }

    const validationInputs: ValidationInputsDto = {
      ticketId: 1,
      courseId: 100
    }

    service.validateTicket(validationInputs).subscribe(validatedTicket => {
      expect(validatedTicket).toBe(ticket);
    });

    const request = httpMock.expectOne('/tickets/validate');

    expect(request.request.method).toBe('POST');
    
    expect(request.request.body).toEqual(validationInputs);

    request.flush(ticket);
  });
});
