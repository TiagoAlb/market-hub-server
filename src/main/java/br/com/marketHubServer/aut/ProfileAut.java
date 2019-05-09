/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.aut;

import br.com.marketHubServer.model.Profile;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

/**
 *
 * @author Tiago Albuquerque
 */
public class ProfileAut extends User {
    private Profile profile;

    public ProfileAut(Profile profile) {
        super(profile.getEmailAddress(),
                profile.getPassword(),
                AuthorityUtils.createAuthorityList(
                        profile.getPermissions().toArray(new String[]{})));
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }
}
