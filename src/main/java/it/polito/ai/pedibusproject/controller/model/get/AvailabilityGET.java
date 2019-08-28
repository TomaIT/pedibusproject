package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Availability;
import it.polito.ai.pedibusproject.database.model.AvailabilityState;
import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.service.interfaces.BusRideService;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
import lombok.Data;

import java.util.Date;

@Data
public class AvailabilityGET implements Comparable<AvailabilityGET> {
    private String id;
    private String idBusRide;
    private BusRideGET busRide;
    private String lineNameOfBusRide;
    private Date startDateOfBusRide;
    private String stopBusName;
    private String idStopBus;
    private String idUser; //Escort
    private AvailabilityState state;

    public AvailabilityGET(Availability availability, BusRideService busRideService,
                           LineService lineService, ReservationService reservationService){
        this.id=availability.getId();
        this.idBusRide=availability.getIdBusRide();
        this.idStopBus=availability.getIdStopBus();
        this.idUser=availability.getIdUser();
        this.state=availability.getState();
        BusRide temp=busRideService.findById(idBusRide);
        this.startDateOfBusRide=temp.getStartTime();
        this.stopBusName=temp.getStopBuses().stream()
                .filter(x->x.getId().equals(idStopBus)).findFirst()
                .orElseThrow(()->new InternalServerErrorException("StopBus not present in BusRide")).getName();
        this.lineNameOfBusRide=lineService.findById(temp.getIdLine()).getName();
        this.busRide=new BusRideGET(temp,reservationService,lineService);
    }

    @Override
    public int compareTo(AvailabilityGET o) {
        if(this.startDateOfBusRide.equals(o.startDateOfBusRide)) return this.id.compareTo(o.id);
        return this.startDateOfBusRide.compareTo(o.startDateOfBusRide);
    }
}
