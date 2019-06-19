package it.polito.ai.pedibusproject.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "busrides")
public class BusRide {
    @Id
    private String id;
    private Long creationTime; //Epoch time
    private String idLine;
    private String idUser; //Escort
    private Long timestampLastStopBus; //Epoch time
    private String idLastStopBus;
}
