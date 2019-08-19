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
<DONE>        -get idChild
<DONE>        -put
<DONE>        -get reservations
<DONE>        -post
    -Line Controller
        -get lines
        -get idLine
    -Message Controller
        -post
        -delete
        -get
        -put
    -Reservation Controller
        -post <fixed>
        -delete
        -get
        -put
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
