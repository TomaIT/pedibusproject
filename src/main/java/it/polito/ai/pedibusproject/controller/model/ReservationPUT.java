package it.polito.ai.pedibusproject.controller.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReservationPUT {
    @NotNull
    private EnumChildGet enumChildGet;
    @NotNull
    @Size(max = 1024)
    private String idStopBus;
    //private String idUser; //preso da Jwt
    @NotNull
    private Long epochTime;
}
