package it.polito.ai.pedibusproject.service.interfaces;

import it.polito.ai.pedibusproject.database.model.Message;

public interface MessageService {
    Message create(String idUserFrom,String idUserTo,String subject,String message,Long creationTime);
    Message findById(String id);
    Message updateReadConfirmById(String id,Long readConfirm);
    void deleteById(String id);
}
