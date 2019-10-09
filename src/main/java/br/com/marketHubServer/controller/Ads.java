/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.controller;

import br.com.marketHubServer.dao.AdDAO;
import br.com.marketHubServer.dao.CategoryDAO;
import br.com.marketHubServer.dao.CategoryItemDAO;
import br.com.marketHubServer.dao.DataSheetDAO;
import br.com.marketHubServer.dao.DataSheetItemDAO;
import br.com.marketHubServer.dao.ImageDAO;
import br.com.marketHubServer.dao.ProductConditionDAO;
import br.com.marketHubServer.dao.ProductDAO;
import br.com.marketHubServer.model.Ad;
import br.com.marketHubServer.model.CategoryItem;
import br.com.marketHubServer.model.DataSheet;
import br.com.marketHubServer.model.DataSheetItem;
import br.com.marketHubServer.model.Image;
import br.com.marketHubServer.model.Product;
import br.com.marketHubServer.model.ProductCondition;
import br.com.marketHubServer.util.Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Tiago Albuquerque
 */
@RestController
@RequestMapping(path = "/api")
public class Ads {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    AdDAO adDAO;
    @Autowired
    ProductDAO productDAO;
    @Autowired
    DataSheetDAO dataSheetDAO;
    @Autowired
    DataSheetItemDAO dataSheetItemDAO;
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    CategoryItemDAO categoryItemDAO;
    @Autowired
    ProductConditionDAO conditionDAO;
    @Autowired
    ImageDAO imageDAO;
    
    private Util util;
    
    @RequestMapping(path = "/ads", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Ad create(@RequestBody Ad ad) throws Exception {
        Product product = ad.getProduct();
        product.setCondition(conditionDAO.findById(product.getCondition().getId()).get());
        DataSheet dataSheet = product.getDataSheet();
        dataSheet.setItems((List<DataSheetItem>) dataSheetItemDAO.saveAll(dataSheet.getItems()));
        product.setDataSheet(dataSheetDAO.save(dataSheet));
        ad.getCategory().setItems((List<CategoryItem>) categoryItemDAO.saveAll(ad.getCategory().getItems()));
        ad.setCategory(categoryDAO.save(ad.getCategory()));
        
        ad.setProduct(productDAO.save(product));
        
        return adDAO.save(ad);
    }
    
    @RequestMapping(path = "/ads/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public Ad update(@PathVariable int id, @RequestBody Ad ad, @RequestParam(required = false, defaultValue = "0") int step) throws Exception {
        Product product = ad.getProduct();
        DataSheet dataSheet = product.getDataSheet();
        dataSheet.setItems((List<DataSheetItem>) dataSheetItemDAO.saveAll(dataSheet.getItems()));
        product.setDataSheet(dataSheetDAO.save(dataSheet));
        ad.setProduct(productDAO.save(product));
        
        return adDAO.save(ad);
    }
    
    @RequestMapping(path = "/ads/{id}/images", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> insertImage(@PathVariable int id,
            @RequestParam("file") MultipartFile uploadfiles) throws Exception {
        Product productUpdate = adDAO.findById(id).get().getProduct();

        try {
            if (!uploadfiles.getContentType().equals("application/octet-stream")) {
                Image image = new Image();
                image.setId(0);
                image.setName(uploadfiles.getName());
                image.setType(uploadfiles.getContentType());
                image.setImage(uploadfiles.getBytes());
                List<Image> images = productUpdate.getImages();
                images.add(imageDAO.save(image));
                productUpdate.setImages(images);
                productDAO.save(productUpdate);
                return new ResponseEntity<>("Imagem cadastrada com sucesso!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Erro!", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new Exception("Erro ao salvar arquivo de imagem");
        }
    }
    
    @RequestMapping(value = "/ads/{id}/image", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> recoverImage(@PathVariable int id)
            throws IOException {
        Image image = imageDAO.findById(id).get();
        if (image.getImage()== null) {
            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentType(MediaType.valueOf("image/jpeg"));
            InputStreamResource img
                    = new InputStreamResource(new ByteArrayInputStream(Files.readAllBytes(Paths.get("user_avatar.png"))));
            return new ResponseEntity<>(img, respHeaders, HttpStatus.OK);
        }
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.valueOf(image.getType()));
        InputStreamResource img = new InputStreamResource(new ByteArrayInputStream(image.getImage()));
        return new ResponseEntity<>(img, respHeaders, HttpStatus.OK);
    }
    
    public boolean publish() throws Exception {
        URL url;

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
       
                        jsonArray = new JSONArray(objectMapper.readTree(response.toString()).toString());
                        
                    JSONArray categoryArray = new JSONArray();
               
                    jsonNode = objectMapper.readTree(categoryArray.toString()); 
                }
        
        return true;
    }
    
    @RequestMapping(path = "/ads", method = RequestMethod.GET)
    public Iterable<Ad> read(@RequestParam(required = false, defaultValue = "0") int page) {
        PageRequest pageRequest = new PageRequest(page, 10);
        return adDAO.findAll(pageRequest);
    }
    
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
