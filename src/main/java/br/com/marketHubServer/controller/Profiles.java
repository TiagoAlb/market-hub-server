/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.controller;

import br.com.marketHubServer.aut.ForbiddenException;
import br.com.marketHubServer.aut.ProfileAut;
import br.com.marketHubServer.dao.AccessTokenDAO;
import br.com.marketHubServer.dao.ImageDAO;
import br.com.marketHubServer.dao.MarketplaceAuthorizationDAO;
import br.com.marketHubServer.dao.MarketplaceDAO;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import br.com.marketHubServer.dao.ProfileDAO;
import br.com.marketHubServer.model.AccessToken;
import br.com.marketHubServer.model.Image;
import br.com.marketHubServer.model.Marketplace;
import br.com.marketHubServer.model.MarketplaceAuthorization;
import br.com.marketHubServer.model.Profile;
import br.com.marketHubServer.model.Session;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import static java.lang.System.out;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import sun.net.www.http.HttpClient;
/**
 *
 * @author Tiago Albuquerque
 */
@RestController
@RequestMapping(path = "/api")
public class Profiles {
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    public static final String SECRET
            = "lSlZbz79sUeirz-XNhY7hw6q6vqONlMEO51xw1OCLEjwnLWbpVo08kyKj-t6dThU4QE1Qtr26g_EClcZR8jCYUlg" +
              "jPbaw_d-L0OTfnB8sE5V6QhxjLslC4F0CUF-MHUR6qAg5bheuOCu30aytetV2Ks6bgkT1y_B78vkCV7nnFIOX20A" +
              "FW3qru0ufUmLOM2iZkvlSgc6z71I-irEGIqkpLE5cC5QIhpSp3d6a0CinuwNUstp1w0JGCRYoNx0unHpSbRMiK-w" +
              "Ty_dThW5OkmMD5lFaeS9LAvn0ovjB_TE2g8bB2Kp1QVQ9uS6jI4oNE22SlSgfMGbowqFC-RQV9tE6SNo60_ikpJQ" +
              "__HfgAtKykex0LDFPcMMzg5FPxIu7Ab0y1CL5gY0qI-AcKEGNkDt4EqDzRDZTAVaAQS8uI1VeTv0OCy6o4sYRDJQ" +
              "yvbI_3NsaUyd_5K2jZ21WQaSGQPGjU4Eyg3yac_AFBpAEx3o52eyTEWkRTjtF6q-0AktzEH1JM1kWuxHQf-6hJkQ" +
              "wk6623rSaEeQ49k2yAbFeQPRlyCgsUxUSQPME_tzkHhgIXAvV1QQ7Eul0kHM9jrr5gLuoHEsDs9Uu09WmVDUkriw" +
              "w3f-AIZUfE-du1FoQSesnwI5JozjoaVUWGX7c7DGdgdAnGGToUQSr0SqHlfnhbEjDw3AbeyQ5DjUGRQVdUHXI_gQ";
    
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    ProfileDAO profileDAO;
    @Autowired
    MarketplaceDAO marketplaceDAO;
    @Autowired
    MarketplaceAuthorizationDAO marketplaceAuthorizationDAO;
    @Autowired
    AccessTokenDAO accessTokenDAO;
    @Autowired
    ImageDAO imageDAO;

    @RequestMapping(path = "/profiles", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Profile create(@AuthenticationPrincipal ProfileAut profileAut, @RequestBody Profile profile) throws Exception {
        if (profileDAO.findByEmailAddress(profile.getEmailAddress()) != null) {
            throw new Exception("E-mail já cadastrado no sistema. Por favor, tente novamente.");
        }
        profile.setId(0);
        profile.setPassword(PASSWORD_ENCODER.encode(profile.getNewPassword()));

        if (profileAut == null || !profileAut.getProfile().getPermissions().contains("administrador")) {
            profile.setType("Company");
            ArrayList<String> permissions = new ArrayList<String>();
            permissions.add("user");
            profile.setPermissions(permissions);
        }
        Profile userSave = profileDAO.save(profile);
        return userSave;
    }
    
    @RequestMapping(path = "/profiles/{profileID}/marketplaces/{marketplaceID}/link", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void linkMarketplace(@PathVariable int profileID, @PathVariable int marketplaceID) throws Exception {
        if (profileDAO.existsById(profileID) && marketplaceDAO.existsById(marketplaceID)) {
            if (profileDAO.findMarketplaceByProfile(profileID, marketplaceID) == null) {
                Optional<Profile> findByIdProfile = profileDAO.findById(profileID);
                Optional<Marketplace> findByIdMarketplace = marketplaceDAO.findById(marketplaceID);
            
                Profile profile = findByIdProfile.get();
                Marketplace marketplace = findByIdMarketplace.get();
                marketplace.setLink_date(new Date(System.currentTimeMillis()));

                List<Marketplace> marketplaces = profile.getMarketplaces();
                marketplaces.add(marketplace);
            
                profile.setMarketplaces(marketplaces);
                profileDAO.save(profile);
            } else throw new ForbiddenException("Marketplace já vinculado a este perfil!");
        }
    }
    
    @RequestMapping(path = "/profiles/{profileID}/marketplaces/{marketplaceID}/authorization", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateAuthorizationCode(@PathVariable int profileID, @PathVariable int marketplaceID, @RequestBody String authorization_code) throws Exception {
        if (profileDAO.existsById(profileID) && marketplaceDAO.existsById(marketplaceID)) {
            if (profileDAO.findMarketplaceByProfile(profileID, marketplaceID) != null) {
                Optional<Marketplace> findByIdMarketplace = marketplaceDAO.findById(marketplaceID);
                Marketplace marketplace = findByIdMarketplace.get();
                System.out.println("ENTROU AQUI "+authorization_code);
                if(authorization_code!=null && authorization_code.equals("cancelToken")) {
                    Integer authorizationID = marketplaceAuthorizationDAO.findAuthorizationByProfileAndMarketplace(profileID, marketplaceID);
                    System.out.println("ENTROU AQUI PRA DELETAR "+authorizationID);
                    if(authorizationID!=null)
                        marketplaceAuthorizationDAO.deleteById(authorizationID);  
                } else {
                
                    /*
                    Integer authorizationID = marketplaceAuthorizationDAO.findAuthorizationByProfileAndMarketplace(profileID, marketplaceID);

                    if(authorizationID!=null) {
                        marketplaceAuthorizationDAO.deleteById(authorizationID);
                    }*/

                    URL url = new URL("https://api.mercadolibre.com/oauth/token");
                    JSONObject access = new JSONObject();
                    access.put("grant_type","authorization_code");
                    access.put("client_id","3919471605726765");
                    access.put("client_secret","zLx9hx8EMQMzSlUYIPzKy97CocfdywxZ");
                    access.put("code",authorization_code.replaceAll("\"", ""));
                    access.put("redirect_uri","http://localhost:3000/#/marketplaces/MLBR001/login?authentication=true");

                    HttpURLConnection http = (HttpURLConnection)url.openConnection();
                    http.setRequestMethod("POST"); 
                    http.setDoOutput(true);
                    http.setRequestProperty("Content-Type", "application/json; utf-8");
                    http.setRequestProperty("Accept", "application/json");
                    http.connect();

                    try(OutputStream os = http.getOutputStream()) {
                        byte[] input = access.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);           
                    }

                    try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(http.getInputStream(), "utf-8"))) {
                          StringBuilder response = new StringBuilder();
                          String responseLine = null;
                          while ((responseLine = br.readLine()) != null) {
                              response.append(responseLine.trim());
                          }
                          JSONObject responseJSON = new JSONObject(response.toString());

                          MarketplaceAuthorization auth = new MarketplaceAuthorization();

                          auth.setId(0);
                          auth.setProfile_id(profileID);
                          auth.setAuthorization_code(authorization_code);

                          List<AccessToken> access_token = new ArrayList<>();
                          List<MarketplaceAuthorization> authorization = marketplace.getAuthorizations();
                          AccessToken token = new AccessToken();
                          token.setId(0);
                          token.setAccess_token(responseJSON.get("access_token").toString());
                          token.setToken_type(responseJSON.get("token_type").toString());
                          token.setExpires_in(Integer.valueOf(responseJSON.get("expires_in").toString()));
                          token.setScope(responseJSON.get("scope").toString());
                          token.setRefresh_token(responseJSON.get("refresh_token").toString());

                          access_token.add(accessTokenDAO.save(token));
                          auth.setAccess(access_token);

                          authorization.add(marketplaceAuthorizationDAO.save(auth));
                          marketplace.setAuthorization(authorization);
                          marketplaceDAO.save(marketplace);
                    }
                }
            } else throw new ForbiddenException("Marketplace não vinculado a este perfil!");
        }
    }
    
    @RequestMapping(path = "/profiles/{id}/marketplaces", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Marketplace> read(@PathVariable int id, @RequestParam(required = false, defaultValue = "0") int page) throws Exception {
        PageRequest pageRequest = new PageRequest(page, 5);
        return marketplaceDAO.findByProfile(id, pageRequest);
    }
    
    @RequestMapping(path = "/profiles/login", method = RequestMethod.GET)
    public ResponseEntity<Profile> login(@AuthenticationPrincipal ProfileAut profileAut)
            throws IllegalArgumentException, UnsupportedEncodingException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        Calendar today = Calendar.getInstance();
        today.add(Calendar.HOUR, 6);
        Date expire = today.getTime();

        String token = JWT.create()
                .withClaim("id", profileAut.getProfile().getId()).
                            withExpiresAt(expire).
                sign(algorithm);
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.set("token", token);
        
        Optional<Profile> findByIdProfile = profileDAO.findById(profileAut.getProfile().getId());
        Profile profile = findByIdProfile.get();
        List sessions = new ArrayList<Session>();
        sessions = profile.getSessions();
        Session session = new Session();
        session.setExpire_date(expire);
        sessions.add(session);
        profile.setSessions(sessions);
        profileDAO.save(profile);

        return new ResponseEntity<>(profileAut.getProfile(), respHeaders, HttpStatus.OK);
    }
    
    @RequestMapping(path = "/profiles/validateLogin", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Profile> validateLogin(@AuthenticationPrincipal ProfileAut profileAut) {
        Optional<Profile> findById = profileDAO.findById(profileAut.getProfile().getId());
        if (findById.isPresent()) {
            return ResponseEntity.ok(findById.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @RequestMapping(path = "/profiles/{id}/image", method = RequestMethod.PUT)
    public ResponseEntity<InputStreamResource> insertImage(@PathVariable int id,
            @RequestParam("file") MultipartFile uploadfiles) throws Exception {
        Optional<Profile> findById = profileDAO.findById(id);
        Profile profileUpdate = findById.get();

        try {
            if (!uploadfiles.getContentType().equals("application/octet-stream")) {
                Image image = new Image();
                image.setId(0);
                image.setName(uploadfiles.getName());
                image.setType(uploadfiles.getContentType());
                image.setImage(uploadfiles.getBytes());
                profileUpdate.setImage(imageDAO.save(image));
                profileDAO.save(profileUpdate);
                return recoverImage(id);
            } else {
                return recoverImage(id);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new Exception("Erro ao salvar arquivo de imagem");
        }
    }
    
    @RequestMapping(value = "/profiles/{id}/image", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> recoverImage(@PathVariable int id)
            throws IOException {
        Optional<Profile> findById = profileDAO.findById(id);
        Profile profile = findById.get();
        if (profile.getImage() == null) {
            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentType(MediaType.valueOf("image/jpeg"));
            InputStreamResource img
                    = new InputStreamResource(new ByteArrayInputStream(Files.readAllBytes(Paths.get("user_avatar.png"))));
            return new ResponseEntity<>(img, respHeaders, HttpStatus.OK);
        }
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.valueOf(profile.getImage().getType()));
        InputStreamResource img = new InputStreamResource(new ByteArrayInputStream(profile.getImage().getImage()));
        return new ResponseEntity<>(img, respHeaders, HttpStatus.OK);
    }
}