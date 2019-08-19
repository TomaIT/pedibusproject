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
                //.antMatchers(HttpMethod.GET,"/rest/busrides/**").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.GET,"/rest/busrides/**/availabilities").hasAnyRole("ESCORT","ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.POST,"/rest/busrides").hasAnyRole("ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.DELETE,"/rest/busrides/**").hasAnyRole("ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.PUT,"/rest/busrides/**").hasAnyRole("ESCORT")

                //Children
                .antMatchers("/rest/children/genders").permitAll()
                //.antMatchers(HttpMethod.GET,"/rest/children/**").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.POST,"/rest/children/**").hasRole("PARENT")
                .antMatchers(HttpMethod.PUT,"/rest/children/**").hasRole("PARENT")
                .antMatchers(HttpMethod.DELETE,"/rest/children/**").hasRole("PARENT")
                .antMatchers(HttpMethod.GET,"/rest/children/**/reservations").hasAnyRole("PARENT","SYS_ADMIN","ADMIN")

                //Lines
                //.antMatchers("/rest/lines/**").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                //.antMatchers("/rest/lines").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")

                //Messages
                //.antMatchers("/rest/messages/**").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                //.antMatchers("/rest/messages").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")

                //Roles
                .antMatchers("/rest/roles").permitAll()
                .antMatchers("/rest/roles/**/users").hasAnyRole("ADMIN","SYS_ADMIN")

                //StopBuses
                .antMatchers("/rest/stopbuses/types").permitAll()
                //.antMatchers("/rest/stopbuses/**").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")

                //Reservations
                //.antMatchers(HttpMethod.GET,"/rest/reservations/**").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.POST,"/rest/reservations").hasRole("PARENT")
                .antMatchers(HttpMethod.PUT,"/rest/reservations/**").hasRole("ESCORT")
                .antMatchers(HttpMethod.DELETE,"/rest/reservations/**").hasAnyRole("PARENT","ADMIN","SYS_ADMIN")

                //Users
                .antMatchers(HttpMethod.POST,"/rest/users").hasAnyRole("ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.POST,"/rest/users/**/uuid").hasAnyRole("ADMIN","SYS_ADMIN")
                //.antMatchers(HttpMethod.GET,"/rest/users/**").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.PUT,"/rest/users/**/role").hasAnyRole("SYS_ADMIN","ADMIN")
                .antMatchers(HttpMethod.PUT,"/rest/users/**/addLine").hasAnyRole("SYS_ADMIN","ADMIN")
                .antMatchers(HttpMethod.PUT,"/rest/users/**/removeLine").hasAnyRole("SYS_ADMIN","ADMIN")
                //.antMatchers(HttpMethod.PUT,"/rest/users/**").hasAnyRole("PARENT","ESCORT","SYS_ADMIN","ADMIN")
                //.antMatchers(HttpMethod.GET,"/rest/users/**/children").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                //.antMatchers(HttpMethod.GET,"/rest/users/**/messages").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                //.antMatchers(HttpMethod.GET,"/rest/users/**/messages/notReadCounter").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                //.antMatchers(HttpMethod.GET,"/rest/users/**/reservations").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                .antMatchers(HttpMethod.GET,"/rest/users/**/availabilities").hasAnyRole("ESCORT","ADMIN","SYS_ADMIN")

                //Default
                .antMatchers("/rest/**").hasAnyRole("PARENT","ESCORT","ADMIN","SYS_ADMIN")
                .antMatchers("/**").permitAll()

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