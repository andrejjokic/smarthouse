/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.customerservice.endpoints;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import smarthouse.entities.Location;
import smarthouse.entities.User;
import smarthouse.types.RegisterUser;

/**
 *
 * @author Andrej
 */

@Path("users")
@Stateless
public class Users {
    
    @PersistenceContext
    EntityManager em;
    
    @GET
    @Path("login")
    public Response checkLogin(@Context HttpHeaders headers) {
        
        List<String> authHeaderValues = headers.getRequestHeader("Authorization");
        
        if (authHeaderValues != null && authHeaderValues.size() > 0) {
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            String password = stringTokenizer.nextToken();
		
            List<User> users = em.createNamedQuery("User.findByUsername",User.class).setParameter("username", username).getResultList();
            
            if (users.size() != 1 || !users.get(0).getPassword().equals(password)) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect username or password").build();
            }

            return Response.status(Response.Status.OK).build();
	}
        
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    
    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_XML)
    public Response registerUser(RegisterUser user) {
        
        List<User> users = em.createNamedQuery("User.findByUsername",User.class).setParameter("username", user.getUsername()).getResultList();
        
        if (users != null && !users.isEmpty()) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        
        List<Location> locations = em.createQuery("SELECT l FROM Location l WHERE l.name = :name",Location.class).setParameter("name", user.getLocation()).getResultList();
        Location location;
        
        if (locations == null || locations.isEmpty()) {
            location = new Location(em.createQuery("SELECT MAX(l.idLoc) FROM Location l",Integer.class).getSingleResult() + 1, user.getLocation(), user.getLongitude(), user.getLatitude());
            em.persist(location);
        } else {
            location = locations.get(0);
        }
        
        User newUser = new User(em.createQuery("SELECT MAX(u.idUsr) FROM User u", Integer.class).getSingleResult() + 1, user.getName(), user.getUsername(), user.getPassword(), user.getSongName(), user.getSongUri());
        newUser.setIdLoc(location);
        
        em.persist(newUser);
        
        return Response.status(Response.Status.CREATED).build();
    }
}
