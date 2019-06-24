package it.polito.ai.pedibusproject.controller.model;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
public class RecoveryUserView {
    @Size(min = 8,max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",flags = Pattern.Flag.UNICODE_CASE)
    private String password;
    @Size(min = 8,max = 32)
    private String verifyPassword;
    @Email
    @Size(min = 1,max = 128)
    private String email;
}
