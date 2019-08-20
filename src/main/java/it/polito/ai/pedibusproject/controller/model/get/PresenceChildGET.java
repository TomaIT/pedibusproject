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

    public PresenceChildGET(Child child, Set<Reservation> reservations){
        this.idChild=child.getId();
        this.nameChild=child.getFirstname()+" "+child.getSurname();
        Reservation temp=reservations.stream()
                .filter(x->x.getIdChild().equals(this.idChild)).findFirst().orElse(null);
        if(temp!=null){
            this.isBooked=true;
            this.idReservation=temp.getId();
            this.isGetIn=temp.getGetIn()!=null;
            this.isGetOut=temp.getGetOut()!=null;
        }
    }

}
