package it.polito.ai.pedibusproject.controller.model.post;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class LoginPOST {
    @Size(max = 128)
    private String email;
    @Size(max = 32)
    private String password;
}
