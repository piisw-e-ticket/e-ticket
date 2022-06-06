import { TicketPeriodicDto } from "./ticketPeriodicDto";
import { TicketSingleDto } from "./ticketSingleDto";

export interface TicketsBoughtDto {
    singleTickets: TicketSingleDto[],
    periodicTickets: TicketPeriodicDto[]
}