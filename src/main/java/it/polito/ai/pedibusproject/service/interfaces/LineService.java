package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Line;

import java.util.Set;

public interface LineService {
    Set<String> aggregateNames();

    Line create(Line line);

    //In realtà non cancella ma setta lo stato della linea a cancellato
    void deleteById(String id);

    Set<Line> findAll();
    Line findById(String id);
}
