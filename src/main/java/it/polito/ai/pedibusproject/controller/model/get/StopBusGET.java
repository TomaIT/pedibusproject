package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Data
public class StopBusGET implements Comparable<StopBusGET>  {
    private String id;
    private StopBusType stopBusType;
    private String name;
    private Long hours; //Minutes from 00:00
    private GeoJsonPoint location;

    public StopBusGET(StopBus stopBus){
        this.id=stopBus.getId();
        this.stopBusType=stopBus.getStopBusType();
        this.name=stopBus.getName();
        this.hours=stopBus.getHours();
        this.location=stopBus.getLocation();
    }

    @Override
    public int compareTo(StopBusGET o) {
        if(this.hours.equals(o.hours))
            return this.id.compareTo(o.id);
        return this.hours.compareTo(o.hours);
    }
}
