package it.polito.ai.pedibusproject.controller.model;

import it.polito.ai.pedibusproject.database.model.Role;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserRolePUT {
    @NotNull
    private Role role;
    @NotNull
    @Size(max = 1024)
    private String idLine;
}
