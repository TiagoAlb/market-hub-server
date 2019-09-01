/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.controller;

import br.com.marketHubServer.aut.ProfileAut;
import br.com.marketHubServer.model.AccessToken;
import br.com.marketHubServer.model.MarketplaceAuthorization;
import br.com.marketHubServer.model.Profile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Tiago Albuquerque
 */
@RestController
@RequestMapping(path = "/api")
public class Ads {
    @Autowired
    private ObjectMapper objectMapper;
    
    @RequestMapping(path = "/profiles/marketplaces/ads/category/search", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<JsonNode> categorySearch(@RequestParam(required = false) String title) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        
        System.out.println(title);
        URL url = new URL("https://api.mercadolibre.com/sites/MLB/category_predictor/predict?title="+title);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("GET"); 
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/json; utf-8");
        http.setRequestProperty("Accept", "application/json");
        http.connect();

        JsonNode jsonNode;
        
            try(BufferedReader br = new BufferedReader(
                new InputStreamReader(http.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    jsonNode = objectMapper.readTree(response.toString());
                }
        
        return new ResponseEntity<JsonNode>(jsonNode, HttpStatus.OK);
    }
    
}
