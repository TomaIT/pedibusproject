package it.polito.ai.pedibusproject.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class MainController {
    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    /*TODO (qui riportiamo quali sono le funzionalità ancora da testare MANTENERE AGGIORNATO)

    -Authentication Controller
<DONE>        -post login
<DONE>        -post recover
<DONE>    -Availability Controller
<DONE>        -post availabilites
<DONE>        -get states
<DONE>        -delete idAva
<DONE>        -get idAva
<DONE>        -put idAva
    -BusRide Controller
        -post
        -delete
        -get idBus
        -put idBus
        -get availabilities
        -get stopbustype,year etc...
    -Child Controller
<DONE>        -get gende
<DONE>        -delete  -> a cosa serve ?? dubbioso.. reservations associate etc ??
                            DS: forse nel caso il figlio cambi scuola... nel caso le reservations associate andrebbero cancellate
                            -> <FIXED>
<DONE>        -get idChild
<DONE>        -put
<DONE>        -get reservations
<DONE>        -post
    -Line Controller
<DONE>        -get lines
<DONE>        -get idLine
    -Message Controller
        -post       -> perché "creationTime" viene passato invece di essere settato dal server quando riceve la POST? -> <FIXED>
                    -> interfaccia rest non modificata, ma il tempo viene comunque settato dal server
                        (questo per non fargli modificare frontend...)
<DONE>        -delete
<DONE>        -get
        -put    -> risponde 404 -> <FIXED> retry...
    -Reservation Controller
<DONE>        -post
<DONE>        -delete
<DONE>        -get
<DONE>        -put    -> "epochTime" penso sia meglio se settato dal server -> <FIXED>
                        -> idem alla post di message...
    -Role Controller
        -get roles
        -get users
    -StopBus Controller
        -get types
        -get idStop
    -User Controller
        -post user
<DONE>        -post uuid
        -get idUser
        -get availabilites
        -get children
        -get messages
        -get counter messages
        -get reservations
        -put idUser
<DONE>        -put addline
<DONE>        -put removeLine
<DONE>        -put role
     */

}
