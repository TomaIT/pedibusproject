package it.polito.ai.pedibusproject.security;

import it.polito.ai.pedibusproject.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    private UnauthorizedException unauthorizedException;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //TODO aggiungere regole di security sui path.
        //@formatter:off
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers ("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/swagger-resources/configuration/ui", "/swagger-ui.html", "/swagger-resources/configuration/security").permitAll()

                //Availabilities
                .antMatchers("/rest/availabilities/states").permitAll()
                .antMatchers(HttpMethod.POST,"/rest/availabilities").hasRole("ESCORT")
                .antMatchers("/rest/availabilities/**").hasAnyRole("ESCORT","ADMIN","SYS_ADMIN")

                //BusRide
                .antMatchers(HttpMethod.GET,"/rest/busrides/**").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.GET,"/rest/busrides/**/availabilities").hasAnyRole("ESCORT","ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.POST,"/rest/busrides").hasAnyRole("ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.DELETE,"/rest/busrides/**").hasAnyRole("ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.PUT,"/rest/busrides/**").hasAnyRole("ESCORT")

                //Children
                .antMatchers("/rest/children/genders").permitAll()
                .antMatchers(HttpMethod.GET,"/rest/children/**").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.POST,"/rest/children/**").hasRole("PARENT")
                .antMatchers(HttpMethod.PUT,"/rest/children/**").hasRole("PARENT")
                .antMatchers(HttpMethod.DELETE,"/rest/children/**").hasRole("PARENT")
                .antMatchers(HttpMethod.GET,"/rest/children/**/reservations").hasAnyRole("PARENT","SYS_ADMIN","ADMIN")



                .antMatchers("/**").permitAll()
                //.antMatchers(HttpMethod.GET, "/vehicles/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(entryPoint())
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
        //@formatter:on
        http.cors();
    }

    @Bean
    public AuthenticationEntryPoint entryPoint(){
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }
}