import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthenticatedGuard } from '../auth/guards/authenticated.guard';
import { TicketOfferComponent } from './components/ticket-offer/ticket-offer.component';
import { TicketPeriodicOrderComponent } from './components/ticket-periodic-order/ticket-periodic-order.component';
import { TicketPeriodicComponent } from './components/ticket-periodic/ticket-periodic.component';
import { TicketPurchasedComponent } from './components/ticket-purchased/ticket-purchased.component';
import { TicketSingleComponent } from './components/ticket-single/ticket-single.component';
import { TicketTimeLimitedComponent } from './components/ticket-time-limited/ticket-time-limited.component';
import { TicketPeriodicGuard } from './guards/ticket-periodic.guard';
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
        path: "purchased",
        component: TicketPurchasedComponent
      },
      {
        path: "offer",
        component: TicketOfferComponent
      },
      {
        path: "offer/single",
        component: TicketSingleComponent
      },
      {
        path: "offer/periodic",
        component: TicketPeriodicComponent
      },
      {
        path: "offer/periodic/order",
        component: TicketPeriodicOrderComponent,
        canActivate: [
          TicketPeriodicGuard
        ]
      },
      {
        path: "offer/time-limited",
        component: TicketTimeLimitedComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TicketRoutingModule { }
