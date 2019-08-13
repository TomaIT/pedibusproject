package it.polito.ai.pedibusproject.service.implementations;

import it.polito.ai.pedibusproject.database.model.Availability;
import it.polito.ai.pedibusproject.database.model.AvailabilityState;
import it.polito.ai.pedibusproject.database.model.BusRide;
import it.polito.ai.pedibusproject.database.repository.AvailabilityRepository;
import it.polito.ai.pedibusproject.database.repository.BusRideRepository;
import it.polito.ai.pedibusproject.database.repository.UserRepository;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.exceptions.NotFoundException;
import it.polito.ai.pedibusproject.service.interfaces.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {
    private AvailabilityRepository availabilityRepository;
    private BusRideRepository busRideRepository;
    private UserRepository userRepository;

    @Autowired
    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository, BusRideRepository busRideRepository, UserRepository userRepository) {
        this.availabilityRepository = availabilityRepository;
        this.busRideRepository = busRideRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Availability findById(String id) {
        return this.availabilityRepository.findById(id).orElseThrow(()->new NotFoundException("Availability"));
    }

    @Override
    public Availability create(String idBusRide, String idStopBus, String idUser, AvailabilityState state) {

        Optional<BusRide> br = this.busRideRepository.findById(idBusRide);
        if(!br.isPresent())
            throw new BadRequestException("Availability <create> not found BusRide");
        if(!br.get().getStopBuses().stream().map(x -> x.getId()).anyMatch(y -> y.equals(idStopBus)))
            throw new BadRequestException("Availability <create> not found StopBus in BusRide");

        // TODO: concorrenza?!
        if(!this.userRepository.existsById(idUser))
            throw new BadRequestException("Availability <create> not found User");

        return this.availabilityRepository.insert(new Availability(idBusRide, idStopBus, idUser, state));
    }

    @Override
    public Availability update(String id, String idStopBus, AvailabilityState state) {
        // TODO
        return null;
    }

    @Override
    public void deleteById(String id) {
        this.availabilityRepository.deleteById(id);
    }

    @Override
    public Set<Availability> findAllByIdBusRide(String idBusRide) {
        return this.availabilityRepository.findAllByIdBusRide(idBusRide);
    }
}
