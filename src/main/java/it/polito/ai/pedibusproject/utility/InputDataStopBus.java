package it.polito.ai.pedibusproject.utility;

import lombok.Data;

@Data
public class InputDataStopBus {
    private String description;
    private Double lon;
    private Double lat;
    private Long time;
}
