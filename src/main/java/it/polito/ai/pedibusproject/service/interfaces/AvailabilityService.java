package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Availability;
import it.polito.ai.pedibusproject.database.model.AvailabilityState;
import it.polito.ai.pedibusproject.database.model.Role;

import java.util.List;
import java.util.Set;

public interface AvailabilityService {
    Availability findById(String id);
    Availability create(String idBusRide, String idStopBus, String idUser, AvailabilityState state);

    Availability update(String idUser, List<Role> roles, String id, String idStopBus, AvailabilityState state);

    void deleteById(String id);
    Set<Availability> findAllByIdBusRide(String idBusRide);
    Set<Availability> findAllByIdUser(String idUser);
}
