package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.StopBus;

public interface StopBusService {
    StopBus insert(StopBus stopBus);
    StopBus findById(String id);
}
