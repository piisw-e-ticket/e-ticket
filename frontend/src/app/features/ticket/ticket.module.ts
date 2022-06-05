import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TicketRoutingModule } from './ticket-routing.module';
import { TicketComponent } from './ticket.component';
import { TicketOfferComponent } from './components/ticket-offer/ticket-offer.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import { ProfileComponent } from './components/profile/profile.component';


@NgModule({
  declarations: [
    TicketComponent,
    TicketOfferComponent,
    NavbarComponent,
    ProfileComponent
  ],
  imports: [
    CommonModule,
    TicketRoutingModule,
    MatToolbarModule,
    MatButtonModule
  ]
})
export class TicketModule { }
