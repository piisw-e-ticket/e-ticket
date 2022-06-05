import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketTimeLimitedComponent } from './ticket-time-limited.component';

describe('TicketTimeLimitedComponent', () => {
  let component: TicketTimeLimitedComponent;
  let fixture: ComponentFixture<TicketTimeLimitedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketTimeLimitedComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TicketTimeLimitedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
