package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Child;
import it.polito.ai.pedibusproject.database.model.Gender;

import java.util.Date;
import java.util.Set;

public interface ChildService {
    //NOTA: Esso ritorna anche i bambini 'cancellati'
    Child findById(String id);
    //NOTA: Esso ritorna anche i bambini 'cancellati'
    Set<Child> findByIdUser(String idUser);

    Set<Child> findAllByIdStopBusOutDef(String idStopBusOutDef);
    Set<Child> findAllByIdStopBusRetDef(String idStopBusRetDef);

    Child create(String idUser, String firstname, String surname, Date birth,Gender gender,String blobBase64,String idStopBusOutDef,String idStopBusRetDef);
    Child update(String id,String firstname, String surname, Date birth,Gender gender,String blobBase64,String idStopBusOutDef,String idStopBusRetDef);
    //Delete Reservations...
    void deleteById(String id);
}
