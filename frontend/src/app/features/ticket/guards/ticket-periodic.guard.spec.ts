import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { TicketPeriodicGuard } from './ticket-periodic.guard';

describe('TicketPeriodicGuard', () => {
  let guard: TicketPeriodicGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ 
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([])
      ]
    });
    guard = TestBed.inject(TicketPeriodicGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
