import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import * as moment from 'moment';
import { of } from 'rxjs';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { UserInfoDto } from 'src/app/features/auth/models/userInfoDto';
import { TicketPeriodicDto } from '../../models/ticketPeriodicDto';
import { TicketsBoughtDto } from '../../models/ticketsBoughtDto';
import { TicketService } from '../../services/ticket.service';

import { ProfileComponent } from './profile.component';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let authService: AuthService;
  let ticketService: TicketService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProfileComponent ],
      imports: [ 
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([])
      ],
      providers: [ 
        AuthService,
        TicketService
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfileComponent);
    authService = TestBed.inject(AuthService);
    ticketService = TestBed.inject(TicketService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should format date', () => {
    const ticket: TicketPeriodicDto = {
      id: 1,
      passengerUsername: 'passenger',
      startDate: '2022-06-11T17:08:49.414',
      endDate: '2022-06-11T18:08:49.414',
      isDiscounted: false
    };

    expect(component.formatDate(ticket.startDate, ticket.endDate)).toBe('11.06.2022, 17:08 – 11.06.2022, 18:08');
  });

  it('time-limited ticket should be valid on initialization', () => {
    const ticket: TicketPeriodicDto = {
      id: 1,
      passengerUsername: 'passenger',
      startDate: moment().format(),
      endDate: moment().add(2, 'hours').format(),
      isDiscounted: false
    };

    expect(component.checkValidity(ticket.startDate, ticket.endDate)).toBe(true);
  });

  it('periodic ticket should be invalid outside of time boundaries', () => {
    const ticket: TicketPeriodicDto = {
      id: 1,
      passengerUsername: 'passenger',
      startDate: moment().subtract(2, 'hours').format(),
      endDate: moment().subtract(1, 'hours').format(),
      isDiscounted: false
    };

    expect(component.checkValidity(ticket.startDate, ticket.endDate)).toBe(false);
  });

  it('periodic ticket should be valid inside of time boundaries', () => {
    const ticket: TicketPeriodicDto = {
      id: 1,
      passengerUsername: 'passenger',
      startDate: moment().subtract(2, 'hours').format(),
      endDate: moment().add(1, 'hours').format(),
      isDiscounted: false
    };

    expect(component.checkValidity(ticket.startDate, ticket.endDate)).toBe(true);
  });

  it('should initialise user and ticket info', fakeAsync(() => {
    const userInfo: UserInfoDto = {
      username: 'passenger',
      role: 'PASSENGER',
      eligibleForDiscount: false
    };

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
    spyOn(authService, 'getUserInfo').and.returnValue(of(userInfo));
    spyOn(ticketService, 'getUserTickets').and.returnValue(of(tickets));
    component.ngOnInit();
    tick();
    expect(component.userInfo).toEqual(userInfo);
    expect(component.ticketsBought).toEqual(tickets);
  }));

  it('should create number of ticket-container appropriate to number of tickets', () => {
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

    const compiled = fixture.nativeElement as HTMLElement;

    component.ticketsBought = tickets;
    component.ticketsType = 'single';
    fixture.detectChanges();
    expect(compiled.querySelectorAll('.ticket-container')?.length).toBe(2);

    component.ticketsType = 'periodic';
    fixture.detectChanges();
    expect(compiled.querySelectorAll('.ticket-container')?.length).toBe(1);
  });
});
