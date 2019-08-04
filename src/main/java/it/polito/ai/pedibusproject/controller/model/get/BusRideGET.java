package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class BusRideGET {
    private String id;
    private String idLine;
    private StopBusType stopBusType;
    private TreeSet<StopBusGET> stopBuses = new TreeSet<>();

    private Integer year;
    private Integer month;
    private Integer day;

    private Date startTime; //Data:ora in cui inizia la corsa
    private Boolean isEnabled; //Stato che indica la cancellazione
    private Set<String> idReservations; //Prenotazioni per tale corsa

    private Long timestampLastStopBus; //Epoch time
    private String idLastStopBus;

    public BusRideGET(BusRide busRide){
        this.id=busRide.getId();
        this.idLine=busRide.getIdLine();
        this.stopBusType=busRide.getStopBusType();
        this.year=busRide.getYear();
        this.month=busRide.getMonth();
        this.day=busRide.getDay();
        this.startTime=busRide.getStartTime();
        this.isEnabled=busRide.getIsEnabled();
        this.idReservations=new HashSet<>(busRide.getIdReservations());
        this.timestampLastStopBus=busRide.getTimestampLastStopBus();
        this.idLastStopBus=busRide.getIdLastStopBus();
        busRide.getStopBuses().forEach(x->this.stopBuses.add(new StopBusGET(x)));
    }
}
