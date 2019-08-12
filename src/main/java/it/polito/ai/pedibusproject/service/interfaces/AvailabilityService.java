package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Availability;
import it.polito.ai.pedibusproject.database.model.AvailabilityState;

public interface AvailabilityService {
    Availability findById(String id);
    Availability create(String idBusRide, String idStopBus, String idUser, AvailabilityState state);
    Availability update(String id, String idStopBus, AvailabilityState state);
    void deleteById(String id);
}
