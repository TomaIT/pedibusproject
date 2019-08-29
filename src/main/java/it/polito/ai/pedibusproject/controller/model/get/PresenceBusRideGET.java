package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.*;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import lombok.Data;

import java.util.*;
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
                              ReservationService reservationService, UserService userService, String username, List roles){
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
        Set<Reservation> reservations = new HashSet<>();
        if(roles.contains(Role.ROLE_ADMIN) || roles.contains(Role.ROLE_SYS_ADMIN)) {
            reservations = reservationService.findAllByIdBusRide(busRide.getId());
        }
        else {
            for(String idChild:childService.findByIdUser(username).stream().map(Child::getId).collect(Collectors.toList()))
                reservations.addAll(reservationService.findAllByIdBusRideAndIdChild(busRide.getId(), idChild));
        }
        for(StopBus stopBus:busRide.getStopBuses()){
            Calendar start=BusRide.getCalendarOnlyDay(busRide.getYear(),busRide.getMonth(),busRide.getDay());
            this.presenceStopBusGETTreeSet.add(
                    new PresenceStopBusGET(stopBus,start,childService,reservations));
        }
    }
}
