package it.polito.ai.pedibusproject.service.implementations;

import it.polito.ai.pedibusproject.database.model.Reservation;
import it.polito.ai.pedibusproject.database.repository.ReservationRepository;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ReservationServiceImpl implements ReservationService {
    private ReservationRepository reservationRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository){
        this.reservationRepository=reservationRepository;
    }

    @Override
    public Set<Reservation> findAllByIdBusRide(String idBusRide) {
        return this.reservationRepository.findAllByIdBusRide(idBusRide);
    }

    @Override
    public Set<Reservation> findAllByIdChild(String idChild) {
        return this.reservationRepository.findAllByIdChild(idChild);
    }

    @Override
    public Set<Reservation> findAllByIdUser(String idUser) {
        return reservationRepository.findAllByIdUser(idUser);
    }

    @Override
    public void deleteById(String id) {
        this.reservationRepository.deleteById(id);
    }
}
