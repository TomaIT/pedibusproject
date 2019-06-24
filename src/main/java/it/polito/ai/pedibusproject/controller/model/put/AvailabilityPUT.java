package it.polito.ai.pedibusproject.controller.model.put;

import it.polito.ai.pedibusproject.database.model.AvailabilityState;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AvailabilityPUT {
    @NotNull
    @Size(max = 1024)
    private String idStopBus;
    @NotNull
    private AvailabilityState state;
}
