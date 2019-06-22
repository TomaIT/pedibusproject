package it.polito.ai.pedibusproject.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "stopbuses")
public class StopBus implements Comparable<StopBus> {
    @Id
    private String id;
    private StopBusType stopBusType;
    private String name;
    private Long hours; //Minutes from 00:00
    private GeoJsonPoint location;

    public void setLocation(Double longitude,Double latitude){
        location = new GeoJsonPoint(longitude,latitude);
    }

    @Override
    public int compareTo(StopBus o) {
        if(this.hours.equals(o.hours))
            return this.id.compareTo(o.id);
        return this.hours.compareTo(o.hours);
    }
}
