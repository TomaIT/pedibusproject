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
    private String id; //Costruito concatenando idChild+stopBusType+year+month+day
    private String idBusRide;
    private String idChild;
    private String idStopBus;
    private String idUser;
    private ReservationState getIn=null;
    private ReservationState getOut=null;

    public Reservation(String idBusRide,String idChild,String idStopBus,String idUser){
        this.idBusRide=idBusRide;
        this.idChild=idChild;
        this.idStopBus=idStopBus;
        this.idUser=idUser;
        this.id=idChild+idBusRide.substring(idBusRide.indexOf('.'));
    }
}
