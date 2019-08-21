package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.User;
import lombok.Data;

@Data
public class LoginGET {
    private String username;
    private String jwtToken;
    private Long expiredEpochTime;
    private UserGET user;

    public LoginGET(String username, String jwtToken, Long expiredEpochTime, UserGET userGET){
        this.username=username;
        this.jwtToken=jwtToken;
        this.expiredEpochTime=expiredEpochTime;
        this.user=userGET;
    }
}
