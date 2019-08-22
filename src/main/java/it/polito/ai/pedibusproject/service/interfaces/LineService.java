package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.controller.model.get.LineEnumGET;
import it.polito.ai.pedibusproject.database.model.Line;
import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;

import java.util.Set;
import java.util.TreeSet;

public interface LineService {
    //Pair key is id and value is name
    Set<LineEnumGET> aggregateNames();

    Line create(Line line);

    Line findByName(String name);

    Line findByIdStopBus(String idStopBus);

    //In realtà non cancella ma setta lo stato della linea a cancellato,
    //inoltre cancella tutte le busride -> prenotazioni e availability, associate
    void deleteById(String id);

    Set<Line> findAll();
    Line findById(String id);

    TreeSet<StopBus> findByIdAndStopBusType(String id, StopBusType stopBusType);
}
