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
        -post login
        -post recover
    -Availability Controller
        -post availabilites
        -get states
        -delete idAva
        -get idAva
        -put idAva
    -BusRide Controller
        -post
        -delete
        -get idBus
        -put idBus
        -get availabilities
        -get stopbustype,year etc...
    -Child Controller
        -get gende
        -delete
        -get idChild
        -put
        -get reservations
        -post
    -Line Controller
        -get lines
        -get idLine
    -Message Controller
        -post
        -delete
        -get
        -put
    -Reservation Controller
        -post
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
        -post uuid
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
