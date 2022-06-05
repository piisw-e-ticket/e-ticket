import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketSingleComponent } from './ticket-single.component';

describe('TicketSingleComponent', () => {
  let component: TicketSingleComponent;
  let fixture: ComponentFixture<TicketSingleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketSingleComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TicketSingleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
