package it.polito.ai.pedibusproject.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Document(collection = "users")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    private String username; // Email
    private String password;
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // @Builder.Default private Set<String> idChildren = new HashSet<>();

    @Builder.Default
    private Set<String> idLines = new HashSet<>();

    @Builder.Default
    private boolean isAccountNonExpired = true;
    @Builder.Default
    private boolean isAccountNonLocked = true;
    @Builder.Default
    private boolean isCredentialsNonExpired = true;
    @Builder.Default
    private boolean isEnabled = false;

    private String firstname;
    private String surname;
    private Date birth;
    //private String country;
    //private String province;
    //private String city;
    private String street;
    //private String cap;
    private String phoneNumber;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map((x)->new SimpleGrantedAuthority(x.name())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
