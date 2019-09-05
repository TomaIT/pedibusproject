package it.polito.ai.pedibusproject.utility.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.polito.ai.pedibusproject.utility.DateHandler;
import lombok.Data;

import java.util.Date;

@Data
public class Holiday {
    @JsonDeserialize(using = DateHandler.class)
    private Date startDate;
    @JsonDeserialize(using = DateHandler.class)
    private Date endDate;
}
