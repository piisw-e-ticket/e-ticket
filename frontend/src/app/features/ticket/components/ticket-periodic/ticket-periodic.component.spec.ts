import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketPeriodicComponent } from './ticket-periodic.component';

describe('TicketPeriodicComponent', () => {
  let component: TicketPeriodicComponent;
  let fixture: ComponentFixture<TicketPeriodicComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketPeriodicComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TicketPeriodicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
