package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.ReservationState;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.StopBusService;
import lombok.Data;

@Data
public class ReservationGET {
    private String id;
    private String idBusRide;
    private String idChild;
    private ChildGET child;
    private String idStopBus;
    private String idUser;
    private ReservationState getIn;
    private ReservationState getOut;
    private ReservationState absent;

    public ReservationGET(Reservation reservation, ChildService childService,
                          StopBusService stopBusService, LineService lineService){
        this.id=reservation.getId();
        this.idBusRide=reservation.getIdBusRide();
        this.idChild=reservation.getIdChild();
        this.idStopBus=reservation.getIdStopBus();
        this.idUser=reservation.getIdUser();
        this.getIn=reservation.getGetIn();
        this.getOut=reservation.getGetOut();
        this.absent=reservation.getAbsent();
        this.child=new ChildGET(childService.findById(idChild),stopBusService,lineService);
    }
}
