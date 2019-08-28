package it.polito.ai.pedibusproject.database.model;

import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.service.interfaces.BusRideService;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Data
@NoArgsConstructor
@Document(collection = "availabilities")
public class Availability {
    @Id
    private String id; //Creato concatenando idUser,idBusRide
    private String idBusRide;
    private String idStopBus;
    private String idUser; //Escort
    private AvailabilityState state;

    public Availability(String idBusRide, String idStopBus, String idUser, AvailabilityState state) {
        this.idBusRide = idBusRide;
        this.idStopBus = idStopBus;
        this.idUser = idUser;
        this.state = state;
        this.id=idUser+"."+idBusRide;
    }

    public String getMessage(BusRide busRide, Line line){
        if(!busRide.getId().equals(idBusRide)||!line.getId().equals(busRide.getIdLine()))
            throw new InternalServerErrorException("Availability.getMessage 1");
        StringBuilder ret=new StringBuilder("Linea: ").append(line.getName()).append("\n")
                .append("Data Corsa: ").append(busRide.getStartTime()).append("\n");
        StopBus first,last;
        switch (busRide.getStopBusType()){
            case Return:
                first=busRide.getStopBuses().first();
                last=busRide.getStopBuses().stream()
                        .filter(x->x.getId().equals(idStopBus)).findFirst()
                        .orElseThrow(()->new InternalServerErrorException("Availability.getMessage 2"));
                break;
            case Outward:
                last=busRide.getStopBuses().last();
                first=busRide.getStopBuses().stream()
                        .filter(x->x.getId().equals(idStopBus)).findFirst()
                        .orElseThrow(()->new InternalServerErrorException("Availability.getMessage 3"));
                break;
            default:
                throw new InternalServerErrorException("Availability.getMessage 4");
        }
        Calendar calendar;


        ret.append("Prima Fermata: ").append(first.getName()).append(" ");
        calendar=BusRide.getCalendarOnlyDay(busRide.getYear(),busRide.getMonth(),busRide.getDay());
        calendar.set(Calendar.MINUTE,first.getHours().intValue());
        ret.append(calendar.getTime()).append("\n");
        ret.append("Ultima Fermata: ").append(last.getName()).append(" ");
        calendar=BusRide.getCalendarOnlyDay(busRide.getYear(),busRide.getMonth(),busRide.getDay());
        calendar.set(Calendar.MINUTE,last.getHours().intValue());
        ret.append(calendar.getTime()).append("\n");

        return ret.toString();
    }
}
