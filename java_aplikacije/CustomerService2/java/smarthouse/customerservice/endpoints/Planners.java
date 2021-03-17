/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.customerservice.endpoints;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import smarthouse.entities.Event;
import smarthouse.entities.Location;
import smarthouse.entities.User;
import smarthouse.messagetypes.PlannerMessage;
import smarthouse.types.AddEvent;
import smarthouse.types.ChangeEvent;

/**
 *
 * @author Andrej
 */

@Path("planners")
@Stateless
public class Planners {
    
    @PersistenceContext
    EntityManager em;
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private ConnectionFactory connFactory;
    
    @Resource(lookup="plannerQueue")
    private Queue plannerQueue;
    
    @POST
    @Path("addEvent")
    @Consumes(MediaType.APPLICATION_XML)
    public Response addEvent(@Context HttpHeaders header,AddEvent addEvent) {

        User user = getUser(header);
        
        Location location;
        
        if (addEvent.getLocationName().equals("")) {                        //at home
            //location = em.createQuery("SELECT u.idLoc FROM User u WHERE u.idUsr = :idUsr",Location.class).setParameter("idusr", user.getIdUsr()).getSingleResult();
            location = user.getIdLoc();
            
        } else {
            List<Location> locations = em.createQuery("SELECT l FROM Location l WHERE l.name = :name",Location.class).setParameter("name", addEvent.getLocationName()).getResultList();

            if (locations == null || locations.isEmpty()) {
                location = new Location(em.createQuery("SELECT MAX(l.idLoc) FROM Location l",Integer.class).getSingleResult() + 1, addEvent.getLocationName(), addEvent.getLocationLon(), addEvent.getLocationLat());
                em.persist(location);
            } else {
                location = locations.get(0);
            }
        }
        
        PlannerMessage pmsg = new PlannerMessage();
        pmsg.setType(PlannerMessage.Type.ADD_EVENT);
        pmsg.setName(addEvent.getName());
        pmsg.setStart(addEvent.getStart());
        pmsg.setDuration(addEvent.getDuration());
        pmsg.setIdLoc(location.getIdLoc());
        pmsg.setIdUsr(user.getIdUsr());
        
        JMSContext context = connFactory.createContext();
        ObjectMessage msg = context.createObjectMessage(pmsg);
        context.createProducer().send(plannerQueue, msg);
        
        return Response.status(Response.Status.CREATED).build();
    }
    
    @GET
    @Path("getEvents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvents(@Context HttpHeaders header) {

        User user = getUser(header);
        
        List<Event> evs = em.createQuery("Select e FROM Event e WHERE e.idUsr.idUsr = :idUsr",Event.class).setParameter("idUsr", user.getIdUsr()).getResultList();
        List<AddEvent> events = new LinkedList<AddEvent>();
        
        for (Event e : evs) {
            events.add(new AddEvent(e.getName(), e.getStart(), e.getDuration(), e.getIdLoc().getName(), e.getIdLoc().getLatitude(), e.getIdLoc().getLongitude()));
        }

        return Response.status(Response.Status.OK).entity(new GenericEntity<List<AddEvent>>(events){}).build();
    }

    @DELETE
    @Path("deleteEvent")
    public Response deleteEvent(@Context HttpHeaders header,@QueryParam("date") String dateS) {
        
        try {
            User user = getUser(header);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date = sdf.parse(dateS);
            
            List<Event> events = em.createQuery("SELECT e FROM Event e WHERE e.start = :date AND e.idUsr.idUsr = :idUsr",Event.class).setParameter("date", date).setParameter("idUsr", user.getIdUsr()).getResultList();
            
            if (events != null && !events.isEmpty()) {
                em.remove(events.get(0));
            }
            
            return Response.status(Response.Status.NO_CONTENT).build();
            
        } catch (ParseException ex) {
            Logger.getLogger(Planners.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PUT
    @Path("changeEvent")
    @Consumes(MediaType.APPLICATION_XML)
    public Response changeEvent(@Context HttpHeaders header, ChangeEvent change) {
        
        User user = getUser(header);
        Event event = em.createQuery("SELECT e FROM Event e WHERE e.start = :start AND e.idUsr.idUsr = :idUsr",Event.class).setParameter("start", change.getPrevDate()).setParameter("idUsr", user.getIdUsr()).getSingleResult();
        
        Location location;
        
        if (change.getLocationName().equals("")) {                        //at home
            location = user.getIdLoc();
            
        } else {
            List<Location> locations = em.createQuery("SELECT l FROM Location l WHERE l.name = :name",Location.class).setParameter("name", change.getLocationName()).getResultList();

            if (locations == null || locations.isEmpty()) {
                location = new Location(em.createQuery("SELECT MAX(l.idLoc) FROM Location l",Integer.class).getSingleResult() + 1, change.getLocationName(), change.getLocationLon(), change.getLocationLat());
                em.persist(location);
            } else {
                location = locations.get(0);
            }
        }
        
        PlannerMessage pmsg = new PlannerMessage();
        pmsg.setType(PlannerMessage.Type.CHANGE_EVENT);
        pmsg.setName(change.getName());
        pmsg.setStart(change.getStart());
        pmsg.setDuration(change.getDuration());
        pmsg.setIdLoc(location.getIdLoc());
        pmsg.setIdUsr(user.getIdUsr());        
        pmsg.setIdEv(event.getIdEv());
        
        JMSContext context = connFactory.createContext();
        ObjectMessage msg = context.createObjectMessage(pmsg);
        context.createProducer().send(plannerQueue, msg);
            
        return Response.status(Response.Status.OK).build();
    }
    
    @GET
    @Path("setRemainder")
    public Response setRemainder(@Context HttpHeaders header,@QueryParam("date") String dateS) {
        try {
            User user = getUser(header);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date = sdf.parse(dateS);
            
            List<Event> events = em.createQuery("SELECT e FROM Event e WHERE e.start = :date AND e.idUsr.idUsr = :idUsr",Event.class).setParameter("date", date).setParameter("idUsr", user.getIdUsr()).getResultList();
            
            if (events != null && !events.isEmpty()) {
                PlannerMessage pmsg = new PlannerMessage();
                pmsg.setType(PlannerMessage.Type.SET_REMAINDER);
                pmsg.setIdEv(events.get(0).getIdEv());
                
                JMSContext context = connFactory.createContext();
                ObjectMessage msg = context.createObjectMessage(pmsg);
                context.createProducer().send(plannerQueue, msg);                
            }
            
            return Response.status(Response.Status.CREATED).build();
            
        } catch (ParseException ex) {
            Logger.getLogger(Planners.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
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
