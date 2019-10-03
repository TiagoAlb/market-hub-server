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
import br.com.marketHubServer.util.Util;
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
import org.json.JSONArray;
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
    
    private Util util;
    
    @RequestMapping(path = "/ads/categories", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<JsonNode> categorySearch(@RequestParam(required = false) String category, @RequestParam(required = false) String name) throws JSONException, IOException {
        URL url;

        if(name==null)
            name="";
        if(category==null)
            category="";
        if(!category.equals(""))
            url = new URL("https://api.mercadolibre.com/categories/"+category);
        else
            url = new URL("https://api.mercadolibre.com/sites/MLB/categories");
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
                    
                    JSONArray jsonArray;
                    
                    if(!category.equals("")) 
                        jsonArray = new JSONArray(objectMapper.readTree(response.toString()).get("children_categories").toString());
                    else
                        jsonArray = new JSONArray(objectMapper.readTree(response.toString()).toString());
                        
                    JSONArray categoryArray = new JSONArray();
                    for(int i=0; i < jsonArray.length(); i++) {
                        JSONObject objJson = new JSONObject(jsonArray.get(i).toString());
                        
                        if(!category.equals("")) 
                            objJson.remove("total_items_in_this_category");

                        if(name.equals("") || objJson.getString("name").toLowerCase().contains(name.toLowerCase()))
                            categoryArray.put(objJson);
                    }
                        jsonNode = objectMapper.readTree(categoryArray.toString()); 
                }
        
        return new ResponseEntity<JsonNode>(jsonNode, HttpStatus.OK);
    }
    
    @RequestMapping(path = "/ads/category/search", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<JsonNode> categoryNavSearch(@RequestParam(required = false, defaultValue = "") String title, @RequestParam(required = false, defaultValue = "") String category) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        
        URL url; 
        
        if(title.equals("") && category.equals(""))
            url = new URL("https://api.mercadolibre.com/sites/MLB/categories");
        else if(title.equals(""))
            url = new URL("https://api.mercadolibre.com/sites/MLB/category_predictor/predict?category_from="+category);
        else
            url = new URL("https://api.mercadolibre.com/sites/MLB/category_predictor/predict?title="+title);
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
           
                if(!title.equals("") || !category.equals("")) {
                    JSONArray jsonArray = new JSONArray(objectMapper.readTree(response.toString()).get("path_from_root").toString());
                    
                    JSONArray categoryArray = new JSONArray();
                    for(int i=0; i < jsonArray.length(); i++) {
                        JSONObject objJson = new JSONObject(jsonArray.get(i).toString());

                        double prediction_probability = objJson.getDouble("prediction_probability");
                        objJson.remove("prediction_probability");

                        if(prediction_probability > 0.5)
                            categoryArray.put(objJson);
                    }

                    jsonNode = objectMapper.readTree(categoryArray.toString());
                } else {
                    jsonNode = objectMapper.readTree(response.toString());
                }
                
                
            }
           
        return new ResponseEntity<JsonNode>(jsonNode, HttpStatus.OK);
    }
    
}
