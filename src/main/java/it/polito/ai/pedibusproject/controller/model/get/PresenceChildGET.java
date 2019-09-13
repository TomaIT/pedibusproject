package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Child;
import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.ReservationState;
import lombok.Data;

@Data
public class PresenceChildGET {
    private String idChild;
    private String nameChild;
    private boolean isBooked=false;
    private String idReservation=null;
    private ReservationState getIn;
    private ReservationState getOut;
    private ReservationState absent;

    public PresenceChildGET(Child child, Reservation reservation){
        this.idChild=child.getId();
        this.nameChild=child.getFirstname()+" "+child.getSurname();
        this.isBooked=true;
        this.idReservation=reservation.getId();
        this.getIn=reservation.getGetIn();
        this.getOut=reservation.getGetOut();
        this.absent=reservation.getAbsent();
    }

}
