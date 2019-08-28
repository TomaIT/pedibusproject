package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Child;
import it.polito.ai.pedibusproject.database.model.Reservation;
import lombok.Data;

import java.util.Set;

@Data
public class PresenceChildGET {
    private String idChild;
    private String nameChild;
    private boolean isBooked=false;
    private String idReservation=null;
    private boolean isGetIn=false;
    private boolean isGetOut=false;
    private boolean isAbsent=false;

    public PresenceChildGET(Child child, Reservation reservation){
        this.idChild=child.getId();
        this.nameChild=child.getFirstname()+" "+child.getSurname();
        this.isBooked=true;
        this.idReservation=reservation.getId();
        this.isGetIn=reservation.getGetIn()!=null;
        this.isGetOut=reservation.getGetOut()!=null;
        this.isAbsent=reservation.getAbsent()!=null;
    }

}
