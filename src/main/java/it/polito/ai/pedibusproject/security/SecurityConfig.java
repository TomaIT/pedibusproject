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
                .antMatchers("/rest/availabilities/states").permitAll()
                .antMatchers(HttpMethod.POST,"/rest/availabilities").hasRole("ESCORT")
                .antMatchers("/rest/availabilities/**").hasAnyRole("ESCORT","ADMIN","SYS_ADMIN")

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