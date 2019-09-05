package it.polito.ai.pedibusproject.utility;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHandler extends StdDeserializer<Date> {

    public DateHandler() {
        this(null);
    }

    public DateHandler(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String date = jsonParser.getText();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            return simpleDateFormat.parse(date);
        } catch (Exception e) {
            return null;
        }
    }
}