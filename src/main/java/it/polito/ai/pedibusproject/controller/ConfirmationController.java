package it.polito.ai.pedibusproject.controller;

import it.polito.ai.pedibusproject.controller.model.ConfirmUserView;
import it.polito.ai.pedibusproject.database.model.ConfirmationToken;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.service.interfaces.ConfirmationTokenService;
import it.polito.ai.pedibusproject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
@RequestMapping("/confirmation")
public class ConfirmationController {
    private ConfirmationTokenService confirmationTokenService;
    private UserService userService;

    @Autowired
    public ConfirmationController(ConfirmationTokenService confirmationTokenService,
                                  UserService userService){
        this.confirmationTokenService=confirmationTokenService;
        this.userService=userService;
    }

    @GetMapping("/{uuid}")
    public String getConfirmView(Model m, @ModelAttribute("confirmUserView") ConfirmUserView confirmUserView,
                                 @PathVariable("uuid") UUID uuid){
        ConfirmationToken temp=this.confirmationTokenService.findByUuid(uuid);
        //Check is expired
        if(this.confirmationTokenService.isExpired(temp))
            return "uuidExpired";
        confirmUserView.setEmail(temp.getUser().getUsername());
        return "confirmUser";
    }

    @PostMapping("/{uuid}")
    public String postRecoverUUID(Model m,@Valid @ModelAttribute("confirmUserView") ConfirmUserView confirmUserView,
                                  BindingResult br,@PathVariable("uuid") UUID uuid){
        if(br.hasErrors()){
            return "confirmUser";
        }
        if(!confirmUserView.getPassword().equals(confirmUserView.getVerifyPassword())){
            m.addAttribute("messageRegisteredErr","Different Password");
            return "confirmUser";
        }
        ConfirmationToken temp = this.confirmationTokenService.findByUuid(uuid);
        if(this.confirmationTokenService.isExpired(temp))
            return "uuidExpired";
        if(!temp.getUser().getUsername().equals(confirmUserView.getEmail())){
            throw new BadRequestException("User-UUID not exist");
        }

        this.userService.confirmRegistration(uuid,confirmUserView.getEmail(),confirmUserView.getPassword(),
                confirmUserView.getFirstname(),confirmUserView.getSurname(),confirmUserView.getBirth(),
                confirmUserView.getStreet(),confirmUserView.getPhoneNumber());

        //TODO angular login
        return "tempLogin";

    }
}
