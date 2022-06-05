import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketOfferComponent } from './ticket-offer.component';

describe('TicketOfferComponent', () => {
  let component: TicketOfferComponent;
  let fixture: ComponentFixture<TicketOfferComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketOfferComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TicketOfferComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
