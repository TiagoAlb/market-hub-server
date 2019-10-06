/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.controller;

import br.com.marketHubServer.aut.ProfileAut;
import br.com.marketHubServer.dao.MarketplaceDAO;
import br.com.marketHubServer.model.Marketplace;
import br.com.marketHubServer.model.Profile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
/**
 *
 * @author Tiago Albuquerque
 */
@RestController
@RequestMapping(path = "/api")
public class Marketplaces {
    @Autowired
    MarketplaceDAO marketplaceDAO;
    
    @RequestMapping(path = "/marketplaces", method = RequestMethod.GET)
    public Iterable<Marketplace> read(@RequestParam(required = false, defaultValue = "0") int page) {
        PageRequest pageRequest = new PageRequest(page, 10);
        return marketplaceDAO.findAll(pageRequest);
    }
    
    @RequestMapping(value = "/marketplaces/{id}/image", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> recoverImage(@PathVariable int id)
            throws IOException {
        Optional<Marketplace> findById = marketplaceDAO.findById(id);
        Marketplace marketplace = findById.get();
        if (marketplace.getImage() == null) {
            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentType(MediaType.valueOf("image/jpeg"));
            InputStreamResource img
                    = new InputStreamResource(new ByteArrayInputStream(Files.readAllBytes(Paths.get("marketplace_avatar.png"))));
            return new ResponseEntity<>(img, respHeaders, HttpStatus.OK);
        }
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.valueOf(marketplace.getImage().getType()));
        InputStreamResource img = new InputStreamResource(new ByteArrayInputStream(marketplace.getImage().getImage()));
        return new ResponseEntity<>(img, respHeaders, HttpStatus.OK);
    }
}
