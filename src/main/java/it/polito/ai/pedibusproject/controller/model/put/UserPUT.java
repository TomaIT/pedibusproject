package it.polito.ai.pedibusproject.controller.model.put;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class UserPUT {
    @Size(min = 8,max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",flags = Pattern.Flag.UNICODE_CASE)
    private String password;
    @Size(min = 8,max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",flags = Pattern.Flag.UNICODE_CASE)
    private String verifyPassword;
    @Size(min = 1,max = 128)
    private String firstname;
    @Size(min = 1,max = 128)
    private String surname;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Valid
    private Date birth;
    @Size(min = 1,max = 128)
    private String street;
    @Size(min = 10,max = 10)
    private String phoneNumber;
}
