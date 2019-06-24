package it.polito.ai.pedibusproject.controller.model.post;

import it.polito.ai.pedibusproject.database.model.Role;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class UserPOST {
    @Email
    @Size(min = 5,max = 256)
    private String email;
    @NotNull
    @Size(min = 1)
    private Set<Role> roles;
}
