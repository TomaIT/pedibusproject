package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
import lombok.Data;

import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Data
public class PresenceBusRideGET {
    private String idLine;
    private String lineName;
    private String idBusRide;
    private StopBusType stopBusType;
    private String idLastStopBus;
    private String nameLastStopBus;
    private TreeSet<PresenceStopBusGET> presenceStopBusGETTreeSet;

    public PresenceBusRideGET(BusRide busRide, LineService lineService, ChildService childService,
                              ReservationService reservationService){
        this.idLine=busRide.getIdLine();
        this.idBusRide=busRide.getId();
        this.stopBusType=busRide.getStopBusType();
        this.idLastStopBus=busRide.getIdLastStopBus();
        if(this.idLastStopBus!=null)
            this.nameLastStopBus=busRide.getStopBuses().stream()
                    .filter(x->x.getId().equals(this.idLastStopBus))
                    .findFirst().orElseThrow(()->new InternalServerErrorException("BusRide Incogruent"))
                    .getName();
        this.lineName=lineService.findById(this.idLine).getName();
        this.presenceStopBusGETTreeSet=new TreeSet<>();
        Set<Reservation> reservations=reservationService.findAllByIdBusRide(busRide.getId());
        for(StopBus stopBus:busRide.getStopBuses()){
            Calendar start=BusRide.getCalendarOnlyDay(busRide.getYear(),busRide.getMonth(),busRide.getDay());
            this.presenceStopBusGETTreeSet.add(
                    new PresenceStopBusGET(stopBus,start,childService,reservations));
        }
    }
}
