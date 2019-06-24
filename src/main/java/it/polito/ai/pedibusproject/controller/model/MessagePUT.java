package it.polito.ai.pedibusproject.controller.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MessagePUT {
    @NotNull
    Boolean readConfirm;
}
