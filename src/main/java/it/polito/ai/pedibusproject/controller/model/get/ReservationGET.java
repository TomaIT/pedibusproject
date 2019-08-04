package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.ReservationState;
import lombok.Data;

@Data
public class ReservationGET {
    private String id;
    private String idBusRide;
    private String idChild;
    private String idStopBus;
    private String idUser;
    private ReservationState getIn;
    private ReservationState getOut;

    public ReservationGET(Reservation reservation){
        this.id=reservation.getId();
        this.idBusRide=reservation.getIdBusRide();
        this.idChild=reservation.getIdChild();
        this.idStopBus=reservation.getIdStopBus();
        this.idUser=reservation.getIdUser();
        this.getIn=reservation.getGetIn();
        this.getOut=reservation.getGetOut();
    }
}
