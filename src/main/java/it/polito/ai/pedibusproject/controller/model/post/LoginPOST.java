package it.polito.ai.pedibusproject.controller.model.post;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class LoginPOST {
    @Email
    @Size(min = 5,max = 128)
    private String email;
    @Size(min = 8,max = 32)
    private String password;
}
