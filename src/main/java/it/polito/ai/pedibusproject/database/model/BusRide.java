package it.polito.ai.pedibusproject.database.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@NoArgsConstructor
@Document(collection = "busrides")
public class BusRide implements Comparable<BusRide> {
    @Id
    private String id; //Creato concatenando idLine,stopBusType,year,month,day
    private String idLine;
    private StopBusType stopBusType;
    private TreeSet<StopBus> stopBuses = new TreeSet<>();

    private Integer year;
    private Integer month;
    private Integer day;

    private Date startTime; //Data:ora in cui inizia la corsa
    private Boolean isEnabled=true; //Stato che indica la cancellazione
    private Set<String> idReservations = new HashSet<>(); //Prenotazioni per tale corsa

    private Long timestampLastStopBus; //Epoch time
    private String idLastStopBus;

    /*
    Year: 1970-...
    Month: 0-11
    Day: 1-31
     */
    public static Calendar getCalendarOnlyDay(Integer year,Integer month,Integer day){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }

    public BusRide(String idLine, StopBusType stopBusType, TreeSet<StopBus> stopBuses,
                   Integer year,Integer month,Integer day){
        this.idLine=idLine;
        this.stopBuses=stopBuses;
        this.stopBusType=stopBusType;
        this.year=year;
        this.month=month;
        this.day=day;
        this.id=idLine+"."+stopBusType+"."+year.toString()+"."+month.toString()+"."+day.toString();

        Calendar calendar=getCalendarOnlyDay(year,month,day);
        calendar.set(Calendar.MINUTE, Objects.requireNonNull(stopBuses.pollFirst()).getHours().intValue());
        this.startTime=calendar.getTime();
    }

    public void addStopBus(StopBus stopBus){
        this.stopBuses.add(stopBus);
    }

    @Override
    public int compareTo(BusRide o) {
        if(this.idLine.equals(o.idLine)){
            if(this.startTime.equals(o.startTime)){
                return this.id.compareTo(o.id);
            }
            return this.startTime.compareTo(o.startTime);
        }
        return this.idLine.compareTo(o.idLine);
    }
}
