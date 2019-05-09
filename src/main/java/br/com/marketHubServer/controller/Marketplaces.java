/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.controller;

import br.com.marketHubServer.aut.ProfileAut;
import br.com.marketHubServer.dao.MarketplaceDAO;
import br.com.marketHubServer.model.Marketplace;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
/**
 *
 * @author Tiago Albuquerque
 */
@RestController
@RequestMapping(path = "/api")
public class Marketplaces {
    @Autowired
    MarketplaceDAO marketplaceDAO;
    
    @RequestMapping(path = "/profiles/marketplaces/available/{id}", method = RequestMethod.GET)
    public Iterable<Marketplace> marketPlacesAvailable(@AuthenticationPrincipal ProfileAut profileAut, @PathVariable int id) {
        if (id != 0) {
            return marketplaceDAO.findAvailable(id);
        } else {
            return marketplaceDAO.findAll();
        }
    }
}
