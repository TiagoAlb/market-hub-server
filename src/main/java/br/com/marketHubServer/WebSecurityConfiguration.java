package br.com.marketHubServer;

import br.com.marketHubServer.aut.DetailsService;
import br.com.marketHubServer.aut.TokenBasedAuthorizationFilter;
import br.com.marketHubServer.controller.Profiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import br.com.marketHubServer.dao.ProfileDAO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tiago Albuquerque
 */
@Component
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DetailsService detailsService;

    @Autowired
    ProfileDAO profileDAO;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(detailsService)
                .passwordEncoder(Profiles.PASSWORD_ENCODER);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //web.ignoring().antMatchers(HttpMethod.POST,"/api/profiles/");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/profiles").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/profiles/{id}/image").permitAll()
                .antMatchers(HttpMethod.GET, "/api/profiles/login/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/marketplaces/").permitAll()
                .antMatchers(HttpMethod.GET, "/api/marketplaces/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/ads/").permitAll()
                .antMatchers(HttpMethod.GET, "/api/ads/**").permitAll()
                .antMatchers("/api/**").authenticated()
                .and().httpBasic()
                .and().addFilterBefore(new TokenBasedAuthorizationFilter(profileDAO), 
                        BasicAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable();
    }
}
