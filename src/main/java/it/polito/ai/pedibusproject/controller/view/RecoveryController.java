package it.polito.ai.pedibusproject.controller.view;

import it.polito.ai.pedibusproject.controller.model.RecoveryUserView;
import it.polito.ai.pedibusproject.database.model.RecoveryToken;
import it.polito.ai.pedibusproject.exceptions.BadRequestException;
import it.polito.ai.pedibusproject.service.interfaces.RecoveryTokenService;
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
@RequestMapping("/recovery")
public class RecoveryController {
    private RecoveryTokenService recoveryTokenService;
    private UserService userService;

    @Autowired
    public RecoveryController(RecoveryTokenService recoveryTokenService,
                              UserService userService){
        this.recoveryTokenService=recoveryTokenService;
        this.userService=userService;
    }

    @GetMapping("/{uuid}")
    public String getRecoveryView(Model m, @ModelAttribute("recoveryUserView") RecoveryUserView recoveryUserView,
                                 @PathVariable("uuid") UUID uuid){
        RecoveryToken temp=this.recoveryTokenService.findByUuid(uuid);
        //Check is expired
        if(this.recoveryTokenService.isExpired(temp))
            return "uuidExpired";
        recoveryUserView.setEmail(temp.getEmail());
        return "recoveryUser";
    }

    @PostMapping("/{uuid}")
    public String postRecoverUUID(Model m, @Valid @ModelAttribute("recoveryUserView") RecoveryUserView recoveryUserView,
                                  BindingResult br, @PathVariable("uuid") UUID uuid){
        if(br.hasErrors()){
            return "recoveryUser";
        }
        if(!recoveryUserView.getPassword().equals(recoveryUserView.getVerifyPassword())){
            m.addAttribute("messageRegisteredErr","Different Password");
            return "recoveryUser";
        }
        RecoveryToken temp = this.recoveryTokenService.findByUuid(uuid);
        if(this.recoveryTokenService.isExpired(temp))
            return "uuidExpired";
        if(!temp.getEmail().equals(recoveryUserView.getEmail())){
            throw new BadRequestException("User-UUID not exist");
        }

        this.userService.updatePassword(recoveryUserView.getEmail(),recoveryUserView.getPassword());
        this.recoveryTokenService.deleteByUuid(uuid);
        //TODO angular login
        return "redirect:http://localhost:4200/login";

    }
}
