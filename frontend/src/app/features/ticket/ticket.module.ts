import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TicketRoutingModule } from './ticket-routing.module';
import { TicketComponent } from './ticket.component';
import { TicketOfferComponent } from './components/ticket-offer/ticket-offer.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatListModule} from '@angular/material/list';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import { ProfileComponent } from './components/profile/profile.component';
import { TicketSingleComponent } from './components/ticket-single/ticket-single.component';
import { TicketPurchasedComponent } from './components/ticket-purchased/ticket-purchased.component';
import { TicketPeriodicComponent } from './components/ticket-periodic/ticket-periodic.component';
import { TicketPeriodicOrderComponent } from './components/ticket-periodic-order/ticket-periodic-order.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';



@NgModule({
  declarations: [
    TicketComponent,
    TicketOfferComponent,
    NavbarComponent,
    ProfileComponent,
    TicketSingleComponent,
    TicketPurchasedComponent,
    TicketPeriodicComponent,
    TicketPeriodicOrderComponent
  ],
  imports: [
    CommonModule,
    TicketRoutingModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatButtonModule,
    MatListModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatInputModule,
  ]
})
export class TicketModule { }
