package it.polito.ai.pedibusproject.controller.model.get;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class LoginGET {
    private String username;
    private String jwtToken;
    private Long expiredEpochTime;

    public LoginGET(String username,String jwtToken){
        this.username=username;
        this.jwtToken=jwtToken;
        //TODO set expired epoch time from JWT
    }
}
