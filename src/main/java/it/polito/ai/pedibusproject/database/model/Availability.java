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
    private String idLine;
    private StopBusType stopBusType;
    private Integer year;
    private Integer month;
    private Integer day;
    private String idUser; //Escort
    private AvailabilityState state;
}
