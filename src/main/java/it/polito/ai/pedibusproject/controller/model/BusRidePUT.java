package it.polito.ai.pedibusproject.controller.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BusRidePUT {
    @NotNull
    private Long timestampLastStopBus; //Epoch time
    @NotNull
    private String idLastStopBus;
}
