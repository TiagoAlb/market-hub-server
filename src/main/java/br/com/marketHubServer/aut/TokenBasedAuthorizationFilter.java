/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.aut;

import static br.com.marketHubServer.controller.Profiles.SECRET;
import br.com.marketHubServer.model.Profile;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import br.com.marketHubServer.dao.ProfileDAO;

/**
 *
 * @author Tiago Albuquerque
 */
public class TokenBasedAuthorizationFilter 
        extends OncePerRequestFilter {
    
    ProfileDAO profileDAO;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token;
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer "))
            token = header.substring(7);
        else
            token = request.getParameter("token");
        if (token != null && !token.isEmpty()) {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            DecodedJWT decode = JWT.require(algorithm).build().verify(token);
            Integer id = decode.getClaim("id").asInt();
            
            Profile profile = profileDAO.findById(id).get();
            
            ProfileAut profileAut = new ProfileAut(profile);
            UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(profileAut
                            , null, profileAut.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        chain.doFilter(request, response);
    }

    public TokenBasedAuthorizationFilter(ProfileDAO profileDAO) {
        super();
        this.profileDAO = profileDAO;
    }
}
