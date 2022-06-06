import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketPurchasedComponent } from './ticket-purchased.component';

describe('TicketPurchasedComponent', () => {
  let component: TicketPurchasedComponent;
  let fixture: ComponentFixture<TicketPurchasedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketPurchasedComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TicketPurchasedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
