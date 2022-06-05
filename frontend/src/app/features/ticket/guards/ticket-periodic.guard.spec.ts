import { TestBed } from '@angular/core/testing';

import { TicketPeriodicGuard } from './ticket-periodic.guard';

describe('TicketPeriodicGuard', () => {
  let guard: TicketPeriodicGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(TicketPeriodicGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
