package it.polito.ai.pedibusproject.service.implementations;

import it.polito.ai.pedibusproject.database.model.Availability;
import it.polito.ai.pedibusproject.database.model.AvailabilityState;
import it.polito.ai.pedibusproject.service.interfaces.AvailabilityService;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {
    @Override
    public Availability findById(String id) {
        // TODO
        return null;
    }

    @Override
    public Availability create(String idBusRide, String idStopBus, String idUser, AvailabilityState state) {
        // TODO
        return null;
    }

    @Override
    public Availability update(String id, String idStopBus, AvailabilityState state) {
        // TODO
        return null;
    }

    @Override
    public void deleteById(String id) {
        // TODO
    }
}
