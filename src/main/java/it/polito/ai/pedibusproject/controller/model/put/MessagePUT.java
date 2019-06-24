package it.polito.ai.pedibusproject.controller.model.put;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MessagePUT {
    @NotNull
    Long readConfirm; //Epoch Time or null
}
