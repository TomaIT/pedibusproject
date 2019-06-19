package it.polito.ai.pedibusproject.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lines")
public class Line {
    @Id
    private String id;
    private String name;
    private Boolean isDeleted = false;
    private String emailAdmin;
    private List<String> idOutStopBuses;
    private List<String> idRetStopBuses;
    private Long creationTime; //Epoch time (lastModified() of configuration file)
    private Long deletedTime; //Epoch time

    public void delete(){
        this.isDeleted=true;
        this.deletedTime = System.currentTimeMillis();
    }
}
