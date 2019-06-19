package it.polito.ai.pedibusproject.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reservations")
public class Reservation {
    @Id
    private String id;
    private Integer year;
    private Integer month;
    private Integer day;
    private String idChild;
    private StopBusType stopBusType;
    private String idStopBus;
    private String idLine;
    private String idUser;
    private ReservationState getIn;
    private ReservationState getOut;
}
