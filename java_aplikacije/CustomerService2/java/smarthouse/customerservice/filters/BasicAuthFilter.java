/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.customerservice.filters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import smarthouse.entities.User;

/**
 *
 * @author Andrej
 */

@Provider
public class BasicAuthFilter implements ContainerRequestFilter {

    @PersistenceContext
    EntityManager em;
    
    @Override
    public void filter(ContainerRequestContext reqContext) throws IOException {
        List<String> authHeaderValues = reqContext.getHeaders().get("Authorization");	//Dohvatamo header Authorization
        
        UriInfo uriInfo = reqContext.getUriInfo();
        List<PathSegment> pathSegments = uriInfo.getPathSegments();
        String endpoint = pathSegments.get(0).getPath();
        String path = null;
        if (pathSegments.size() > 1) 
            path = pathSegments.get(1).getPath();
        
        if (endpoint.equals("users") && path != null && (path.equals("login") || path.equals("register"))) return;
	
	if (authHeaderValues != null && authHeaderValues.size() > 0) {
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            String password = stringTokenizer.nextToken();
		
            List<User> users = em.createNamedQuery("User.findByUsername",User.class).setParameter("username", username).getResultList();

            if (users.size() != 1 || !users.get(0).getPassword().equals(password)) {
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect username or password").build();
                reqContext.abortWith(response);
                return;
            }
            
            return;
	}
        
	Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Please send credentials").build();
        reqContext.abortWith(response);
        return;
    }
}
