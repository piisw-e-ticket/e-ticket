import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DateAdapter } from '@angular/material/core';
import { RouterTestingModule } from '@angular/router/testing';

import { TicketPeriodicOrderComponent } from './ticket-periodic-order.component';

describe('TicketPeriodicOrderComponent', () => {
  let component: TicketPeriodicOrderComponent;
  let fixture: ComponentFixture<TicketPeriodicOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TicketPeriodicOrderComponent ],
      imports: [ 
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([])
      ],
      providers: [
        DateAdapter
      ]
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
