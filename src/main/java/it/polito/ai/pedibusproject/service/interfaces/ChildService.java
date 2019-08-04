package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Child;
import it.polito.ai.pedibusproject.database.model.Gender;

import java.util.Date;
import java.util.Set;

public interface ChildService {
    Child findById(String id);
    Set<Child> findByIdUser(String idUser);
    Child create(String idUser, String firstname, String surname, Date birth,Gender gender,String blobBase64,String idStopBusOutDef,String idStopBusRetDef);
    Child update(String id,String firstname, String surname, Date birth,Gender gender,String blobBase64,String idStopBusOutDef,String idStopBusRetDef);
    //TODO delete non vera solo boolean
    void deleteById(String id);
}
