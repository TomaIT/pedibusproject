package it.polito.ai.pedibusproject.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Data
public class InputDataLine {
    private String name;
    private String emailAdmin;
    private List<InputDataStopBus> outwardLine;
    private List<InputDataStopBus> returnLine;

    public static InputDataLine loadData(File file) throws IOException {
        ObjectMapper mapper=new ObjectMapper();
        InputDataLine inputDataLine=mapper.readValue(file,InputDataLine.class);
        return inputDataLine;
    }

    public static InputDataLine loadData(InputStream is) throws IOException{
        ObjectMapper mapper=new ObjectMapper();
        InputDataLine inputDataLine=mapper.readValue(is,InputDataLine.class);
        return inputDataLine;
    }
}
