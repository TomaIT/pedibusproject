package it.polito.ai.pedibusproject.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineEnum {
    private String idLine;
    private String lineName;
}
