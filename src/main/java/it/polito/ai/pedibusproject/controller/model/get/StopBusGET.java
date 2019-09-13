package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import it.polito.ai.pedibusproject.service.interfaces.LineService;
import lombok.Data;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Data
public class StopBusGET implements Comparable<StopBusGET>  {
    private String id;
    private StopBusType stopBusType;
    private String name;
    private Long hours; //Minutes from 00:00
    private GeoJsonPoint location;
    private String lineName;
    private String idLine;

    public StopBusGET(StopBus stopBus, LineService lineService){
        this.id=stopBus.getId();
        this.stopBusType=stopBus.getStopBusType();
        this.name=stopBus.getName();
        this.hours=stopBus.getHours();
        this.location=stopBus.getLocation();
        Line temp=lineService.findByIdStopBus(id);
        this.lineName=temp.getName();
        this.idLine=temp.getId();
    }
    public StopBusGET(StopBus stopBus, Line line){
        this.id=stopBus.getId();
        this.stopBusType=stopBus.getStopBusType();
        this.name=stopBus.getName();
        this.hours=stopBus.getHours();
        this.location=stopBus.getLocation();
        Line temp=line;
        this.lineName=temp.getName();
        this.idLine=temp.getId();
    }

    @Override
    public int compareTo(StopBusGET o) {
        if(this.hours.equals(o.hours))
            return this.id.compareTo(o.id);
        return this.hours.compareTo(o.hours);
    }
}
