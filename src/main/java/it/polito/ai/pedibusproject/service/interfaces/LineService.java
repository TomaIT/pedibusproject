package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;
import javafx.util.Pair;

import java.util.Set;
import java.util.TreeSet;

public interface LineService {
    //Pair key is id and value is name
    Set<Pair<String,String>> aggregateNames();

    Line create(Line line);

    Line findByName(String name);

    //In realtà non cancella ma setta lo stato della linea a cancellato
    //TODO Quando si 'cancella' una linea come gestire le corse e le prenotazioni ?
    void deleteById(String id);

    Set<Line> findAll();
    Line findById(String id);

    TreeSet<StopBus> findByIdAndStopBusType(String id, StopBusType stopBusType);
}
