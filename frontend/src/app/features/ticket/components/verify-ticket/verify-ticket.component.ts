import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TicketValidatedDto } from '../../models/ticketValidatedDto';
import { ValidationInputsDto } from '../../models/validationInputsDto';
import { TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-verify-ticket',
  templateUrl: './verify-ticket.component.html',
  styleUrls: ['./verify-ticket.component.css']
})
export class VerifyTicketComponent {

  ticketsType: string = 'single';

  error: String | null = null;
  validationResult: boolean | null = null;

  constructor(private ticketService: TicketService) {}

  verificationForm = new FormGroup({
    ticketId: new FormControl('', [Validators.required]),
    courseId: new FormControl('', [Validators.required]),
  });

  get ticketId() { return this.verificationForm.get("ticketId") }
  get courseId() { return this.verificationForm.get("courseId") }

  onSubmit() {
    const validationInputDto = this.verificationForm.value as ValidationInputsDto;
    this.ticketService.validateTicket(validationInputDto)
      .subscribe(val => {
        const validatedTicket = val as TicketValidatedDto;
        if (validatedTicket.errors?.length > 0) {
          this.error = validatedTicket.errors[0];
        } else this.error = null;
        this.validationResult = validatedTicket.isValid;
      })
  }
}
