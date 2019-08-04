package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Availability;
import it.polito.ai.pedibusproject.database.model.AvailabilityState;
import lombok.Data;

@Data
public class AvailabilityGET {
    private String id;
    private String idBusRide;
    private String idStopBus;
    private String idUser; //Escort
    private AvailabilityState state;

    public AvailabilityGET(Availability availability){
        this.id=availability.getId();
        this.idBusRide=availability.getIdBusRide();
        this.idStopBus=availability.getIdStopBus();
        this.idUser=availability.getIdUser();
        this.state=availability.getState();
    }
}
