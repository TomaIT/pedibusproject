package it.polito.ai.pedibusproject.controller.model;

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
    @Size(min = 1970,max = 4096)
    private Integer year;
    @NotNull
    @Size(min = 0,max = 11)
    private Integer month;
    @NotNull
    @Size(min = 1,max = 31)
    private Integer day;
}
