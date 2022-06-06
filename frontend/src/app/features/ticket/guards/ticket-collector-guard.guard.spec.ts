import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { TicketCollectorGuard } from './ticket-collector-guard.guard';

describe('TicketCollectorGuard', () => {
  let guard: TicketCollectorGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ 
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([])
      ]
    });
    guard = TestBed.inject(TicketCollectorGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
