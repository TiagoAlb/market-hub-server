/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.aut;

import br.com.marketHubServer.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import br.com.marketHubServer.dao.ProfileDAO;

/**
 *
 * @author Tiago Albuquerque
 */
@Component
public class DetailsService implements UserDetailsService {
    @Autowired
    ProfileDAO profileDAO;
    @Override
    public UserDetails loadUserByUsername(String email) 
            throws UsernameNotFoundException {
        Profile profile = profileDAO.findByEmailAddress(email);
        if (profile == null) {
            throw new UsernameNotFoundException(email + " não existe!");
        }
        return new ProfileAut(profile);
    }
}