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
<DONE>        -post      -> ho aggiunto il controllo che la data sia futura
<DONE>        -delete
<DONE>        -get idBus
<DONE>        -put idBus      -> ho settato "timestampLastStopBus" nel controller
<DONE>        -get availabilities      -> ritorna sempre 200 anche se idBusRide non esiste, è corretto?
<DONE>        -get stopbustype,year etc...      -> ritorna sempre 200 anche se idBusRide non esiste, è corretto?
<DONE>        -get downloadInfo
    -Child Controller
<DONE>        -get gende
<DONE>        -delete  -> a cosa serve ?? dubbioso.. reservations associate etc ??
                            DS: forse nel caso il figlio cambi scuola... nel caso le reservations associate andrebbero cancellate
                            -> <FIXED>
<DONE>        -get idChild
<DONE>        -put
<DONE>        -get reservations
<DONE>        -post
    -Line Controller        -> se un PARENT non ha accesso come fa a sceglire fermate di default?
<DONE>        -get lines
<DONE>        -get idLine
    -Message Controller
        -post       -> perché "creationTime" viene passato invece di essere settato dal server quando riceve la POST? -> <FIXED>
                    -> interfaccia rest non modificata, ma il tempo viene comunque settato dal server
                        (questo per non fargli modificare frontend...)
<DONE>        -delete
<DONE>        -get
<DONE>        -put        -> "readConfirm" è l'epoch time, settaggio dal server?? -> <FIXED>
    -Reservation Controller
<DONE>        -post
<DONE>        -delete
<DONE>        -get
<DONE>        -put    -> "epochTime" penso sia meglio se settato dal server -> <FIXED>
                        -> idem alla post di message...
    -Role Controller
<DONE>        -get roles
<DONE>        -get users
    -StopBus Controller
<DONE>        -get types
<DONE>        -get idStop
    -User Controller
<DONE>        -post user
<DONE>        -post uuid
<DONE>        -get idUser
<DONE>        -get availabilites
<DONE>        -get children
<DONE>        -get messages
<DONE>        -get counter messages
<DONE>        -get reservations
<DONE>        -put idUser
<DONE>        -put addLine
<DONE>        -put addRole
<DONE>        -put removeRole
        -put disable    -> setta a false "isAccountNonLocked", non "isEnabled" come mi sarei aspettato

<DONE>        -put addline
<DONE>        -put removeLine
<DONE>        -put role
     */

}
