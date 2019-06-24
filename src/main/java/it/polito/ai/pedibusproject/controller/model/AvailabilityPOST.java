package it.polito.ai.pedibusproject.controller.model;

import it.polito.ai.pedibusproject.database.model.AvailabilityState;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AvailabilityPOST {
    @NotNull
    @Size(max = 1024)
    private String idBusRide;
    @NotNull
    @Size(max = 1024)
    private String idStopBus;
    @NotNull
    @Size(max = 1024)
    private String idUser; //Escort //Jwt ??
    @NotNull
    private AvailabilityState state;
}
