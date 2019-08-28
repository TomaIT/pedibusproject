package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Child;
import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class PresenceStopBusGET implements Comparable<PresenceStopBusGET> {
    private String idStopBus;
    private String nameStopBus;
    private Date hours;
    private Set<PresenceChildGET> presenceChildGETSet;

    public PresenceStopBusGET(StopBus stopBus, Calendar startTime, ChildService childService,
                              Set<Reservation> reservations){
        this.idStopBus=stopBus.getId();
        this.nameStopBus=stopBus.getName();
        startTime.set(Calendar.MINUTE,stopBus.getHours().intValue());
        this.hours=startTime.getTime();
        this.presenceChildGETSet=new HashSet<>();
        for(Reservation reservation:reservations.stream().filter(r -> r.getIdStopBus().equals(stopBus.getId())).collect(Collectors.toList())) {
            Child child = childService.findById(reservation.getIdChild());
            this.presenceChildGETSet.add(new PresenceChildGET(child, reservation));
        }
    }

    @Override
    public int compareTo(PresenceStopBusGET o) {
        return this.hours.compareTo(o.hours);
    }
}
