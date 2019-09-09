package it.polito.ai.pedibusproject.controller;

import it.polito.ai.pedibusproject.controller.model.RecoveryUserView;
import it.polito.ai.pedibusproject.database.model.RecoveryToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class AngularController {

    @GetMapping("/login") public String a(){ return "index"; }
    @GetMapping("/forgotPassword") public String b(){ return "index"; }
    @GetMapping("/createUser") public String c(){ return "index"; }
    @GetMapping("/children") public String d(){ return "index"; }
    @GetMapping("/reservation") public String e(){ return "index"; }
    @GetMapping("/messages/{id}") public String f(){ return "index"; }
    @GetMapping("/messages") public String j(){ return "index"; }
    @GetMapping("/children/register") public String g(){ return "index"; }
    @GetMapping("/children/update/{id}") public String h(){ return "index"; }
    @GetMapping("/attendees/manage/{idBusRide}/{idCurrentStopBus}") public String i(){ return "index"; }
    @GetMapping("/createAvailabilities") public String l(){ return "index"; }
    @GetMapping("/viewAvailabilities") public String m(){ return "index"; }
    @GetMapping("/shiftManage") public String n(){ return "index"; }
    @GetMapping("/shiftManage/{id}") public String o(){ return "index"; }
    @GetMapping("/manageUsers") public String p(){ return "index"; }
    @GetMapping("/manageUsers/{id}") public String q(){ return "index"; }
    @GetMapping("/mapLines") public String r(){ return "index"; }
    @GetMapping("/mapLines/{id}") public String s(){ return "index"; }
    @GetMapping("/userProfile") public String t(){ return "index"; }
    @GetMapping("/busridesEscort") public String u(){ return "index"; }
    @GetMapping("/stateBusRide") public String v(){ return "index"; }
    @GetMapping("/stateBusRide/{idLine}/{stopBusType}/{data}") public String z(){ return "index"; }
    @GetMapping("/home") public String w(){ return "index"; }
    @GetMapping("/") public String y(){ return "index"; }
    @GetMapping("/createLine") public String k(){ return "index"; }
}
