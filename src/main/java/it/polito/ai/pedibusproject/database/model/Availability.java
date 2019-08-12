package it.polito.ai.pedibusproject.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "availabilities")
public class Availability {
    @Id
    private String id;
    private String idBusRide;
    private String idStopBus;
    private String idUser; //Escort
    private AvailabilityState state;

    public Availability(String idBusRide, String idStopBus, String idUser, AvailabilityState state) {
        this.idBusRide = idBusRide;
        this.idStopBus = idStopBus;
        this.idUser = idUser;
        this.state = state;
    }
}
