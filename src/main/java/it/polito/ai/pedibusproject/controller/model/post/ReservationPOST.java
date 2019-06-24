package it.polito.ai.pedibusproject.controller.model.post;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReservationPOST {
    @NotNull
    @Size(max = 1024)
    private String idBusRide;
    @NotNull
    @Size(max = 1024)
    private String idChild;
    //private String idUser; //preso da Jwt
    @NotNull
    @Size(max = 1024)
    private String idStopBus;
}
