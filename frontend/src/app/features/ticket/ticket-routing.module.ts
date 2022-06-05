import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthenticatedGuard } from '../auth/guards/authenticated.guard';
import { TicketOfferComponent } from './components/ticket-offer/ticket-offer.component';
import { TicketSingleComponent } from './components/ticket-single/ticket-single.component';
import { TicketComponent } from './ticket.component';

const routes: Routes = [
  {
    path: "ticket",
    component: TicketComponent,
    canActivate: [
      AuthenticatedGuard
    ],
    children: [
      {
        path: "offer",
        component: TicketOfferComponent
      },
      {
        path: "offer/single",
        component: TicketSingleComponent
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TicketRoutingModule { }
