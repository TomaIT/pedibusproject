package it.polito.ai.pedibusproject.controller.model.post;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class RecoverPOST {
    @Email
    @Size(min = 5,max = 128)
    private String email;
}
