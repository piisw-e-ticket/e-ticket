import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketPeriodicOrderComponent } from './ticket-periodic-order.component';

describe('TicketPeriodicOrderComponent', () => {
  let component: TicketPeriodicOrderComponent;
  let fixture: ComponentFixture<TicketPeriodicOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketPeriodicOrderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TicketPeriodicOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
