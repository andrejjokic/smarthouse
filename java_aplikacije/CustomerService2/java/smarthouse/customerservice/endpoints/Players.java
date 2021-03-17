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
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import smarthouse.entities.User;
import smarthouse.messagetypes.*;

/**
 *
 * @author Andrej
 */

@Path("players")
@Stateless
public class Players {
        
    @PersistenceContext
    EntityManager em;
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private ConnectionFactory connFactory;
    
    @Resource(lookup="playerQueue")
    private Queue playerQueue;
    
    @GET
    @Path("playSong")
    public Response playSong(@Context HttpHeaders header,@QueryParam("name") String name,@QueryParam("uri") String uri) {

        User user = getUser(header);
        
        JMSContext context = connFactory.createContext();
        JMSProducer producer = context.createProducer();
        ObjectMessage msg = context.createObjectMessage(new PlayerMessage(user.getIdUsr(), uri, name, PlayerMessage.Type.PLAY_SONG));
        producer.send(playerQueue, msg);
        
        return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("playedSongs")
    public Response showPlayedSongs(@Context HttpHeaders header) {
        
        Integer userId = getUser(header).getIdUsr();
     
        JMSContext context = connFactory.createContext();
        ObjectMessage msg = context.createObjectMessage(new PlayerMessage(userId, null, null, PlayerMessage.Type.SHOW_PLAYED_SONGS));
        context.createProducer().send(playerQueue, msg);
        
        return Response.status(Response.Status.OK).build();
    }
    
    private User getUser(HttpHeaders header) {
        List<String> authHeaderValues = header.getRequestHeader("Authorization");

        String authHeaderValue = authHeaderValues.get(0);
	String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
        StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        String username = stringTokenizer.nextToken();
        
        return em.createNamedQuery("User.findByUsername",User.class).setParameter("username", username).getSingleResult();
    }   
}
