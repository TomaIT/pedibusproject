package it.polito.ai.pedibusproject.controller.model.get;

import it.polito.ai.pedibusproject.database.model.Role;
import it.polito.ai.pedibusproject.database.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserGET {
    private String username; // Email
    //private String password;
    private Set<Role> roles;
    private Set<String> idLines;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private String firstname;
    private String surname;
    private Date birth;
    private String street;
    private String phoneNumber;

    public UserGET(User user){
        this.username=user.getUsername();
        this.roles=new HashSet<>(user.getRoles());
        this.idLines=new HashSet<>(user.getIdLines());
        this.isAccountNonExpired=user.isAccountNonExpired();
        this.isAccountNonLocked=user.isAccountNonLocked();
        this.isCredentialsNonExpired=user.isCredentialsNonExpired();
        this.isEnabled=user.isEnabled();
        this.firstname=user.getFirstname();
        this.surname=user.getSurname();
        this.birth=user.getBirth();
        this.street=user.getStreet();
        this.phoneNumber=user.getPhoneNumber();
    }
}
