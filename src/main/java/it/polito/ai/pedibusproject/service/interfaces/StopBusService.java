package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.StopBus;
import it.polito.ai.pedibusproject.database.model.StopBusType;

import java.util.Set;

public interface StopBusService {
    StopBus insert(StopBus stopBus);
    StopBus findById(String id);
    Set<StopBus> findAllByStopBusType(StopBusType stopBusType);
    Set<StopBus> findAll();
}
