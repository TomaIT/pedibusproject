package it.polito.ai.pedibusproject.controller.model.post;

import it.polito.ai.pedibusproject.database.model.StopBusType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class BusRidePOST {
    @NotNull
    @Size(max = 1024)
    private String idLine;
    @NotNull
    private StopBusType stopBusType;
    @NotNull
    private Integer year;
    @NotNull
    private Integer month;
    @NotNull
    private Integer day;
}
