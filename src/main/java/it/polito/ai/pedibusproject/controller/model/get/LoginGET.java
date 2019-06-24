package it.polito.ai.pedibusproject.controller.model.get;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginGET {
    private String username;
    private String jwtToken;
}
