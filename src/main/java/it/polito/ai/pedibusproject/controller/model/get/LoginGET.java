package it.polito.ai.pedibusproject.controller.model.get;

import lombok.Data;

@Data
public class LoginGET {
    private String username;
    private String jwtToken;
    private Long expiredEpochTime;

    public LoginGET(String username,String jwtToken,Long expiredEpochTime){
        this.username=username;
        this.jwtToken=jwtToken;
        this.expiredEpochTime=expiredEpochTime;
    }
}
