package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Child;
import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
        Set<Child> children=null;
        switch (stopBus.getStopBusType()){
            case Return:
                children=childService.findAllByIdStopBusRetDef(stopBus.getId());
                break;
            case Outward:
                children=childService.findAllByIdStopBusOutDef(stopBus.getId());
                break;
            default:
                throw new InternalServerErrorException("StopBusType not determinate");
        }
        for(Child child:children){
            this.presenceChildGETSet.add(new PresenceChildGET(child,reservations));
        }
    }

    @Override
    public int compareTo(PresenceStopBusGET o) {
        return this.hours.compareTo(o.hours);
    }
}
