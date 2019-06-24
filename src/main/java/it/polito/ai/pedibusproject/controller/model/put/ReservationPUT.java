package it.polito.ai.pedibusproject.controller.model.put;

import it.polito.ai.pedibusproject.controller.model.get.EnumChildGET;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReservationPUT {
    @NotNull
    private EnumChildGET enumChildGet;
    @NotNull
    @Size(max = 1024)
    private String idStopBus;
    //private String idUser; //preso da Jwt
    @NotNull
    private Long epochTime;
}
