package it.polito.ai.pedibusproject.controller.model.post;

import it.polito.ai.pedibusproject.database.model.Gender;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class ChildPOST {
    @NotNull
    @Size(max = 64)
    private String firstname;
    @NotNull
    @Size(max = 64)
    private String surname;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Valid
    private Date birth;
    @NotNull
    private Gender gender;
    private String blobBase64; //Photo ??
    private String idStopBusOutDef;
    private String idStopBusRetDef;
}
