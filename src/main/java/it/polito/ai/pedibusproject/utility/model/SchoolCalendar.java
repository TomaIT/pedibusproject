package it.polito.ai.pedibusproject.utility.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.polito.ai.pedibusproject.utility.DateHandler;
import it.polito.ai.pedibusproject.utility.InputDataLine;
import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Data
public class SchoolCalendar {
    @JsonDeserialize(using = DateHandler.class)
    private Date startDate;
    @JsonDeserialize(using = DateHandler.class)
    private Date endDate;
    private boolean saturdayAtSchool;
    private List<Holiday> holidays;

    public static SchoolCalendar loadData(InputStream is) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SchoolCalendar schoolCalendar = mapper.readValue(is, SchoolCalendar.class);
        return schoolCalendar;
    }
}
