export interface TicketSingleDto {
    id: number,
    passengerUsername: string,
    courseId: number | null,
    isDiscounted: boolean,
    isPunched: boolean
}