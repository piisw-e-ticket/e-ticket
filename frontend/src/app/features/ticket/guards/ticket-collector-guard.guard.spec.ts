import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { TicketCollectorGuard } from './ticket-collector-guard.guard';

describe('TicketCollectorGuard', () => {
  let guard: TicketCollectorGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ 
        HttpClientTestingModule
      ]
    });
    guard = TestBed.inject(TicketCollectorGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
