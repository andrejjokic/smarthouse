/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.customerservice.endpoints;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.LinkedList;
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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import smarthouse.entities.Alarm;
import smarthouse.entities.User;
import smarthouse.messagetypes.AlarmMessage;
import smarthouse.types.CreateAlarm;
import smarthouse.types.OldAlarm;

/**
 *
 * @author Andrej
 */

@Path("alarms")
@Stateless
public class Alarms {
    
    @PersistenceContext
    EntityManager em;
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    private ConnectionFactory connFactory;
    
    @Resource(lookup="alarmQueue")
    private Queue alarmQueue;
    
    @PUT
    @Path("setSong")
    public Response setSong(@Context HttpHeaders header,@QueryParam("name") String name, @QueryParam("uri") String uri) {
        
        User user = getUser(header);
        
        user.setAlarmSongName(name);
        user.setAlarmSongUri(uri);
        
        //em.persist(user);
        
        return Response.status(Response.Status.OK).build();
    }
    
    @POST
    @Path("setAlarm")
    @Consumes(MediaType.APPLICATION_XML)
    public Response setAlarm(CreateAlarm newAlarm) {
        int userId = em.createQuery("SELECT u.idUsr FROM User u WHERE u.username = :username",Integer.class).setParameter("username", newAlarm.getUser()).getSingleResult();
        
        JMSContext context = connFactory.createContext();
        ObjectMessage msg = context.createObjectMessage(new AlarmMessage(userId,newAlarm.getAlarmId(), newAlarm.getTime(), newAlarm.getPeriod()));
        context.createProducer().send(alarmQueue, msg);
        
        return Response.status(Response.Status.CREATED).build();
    }
    
    @GET
    public Response getAlarms(@Context HttpHeaders header) {

        User user = getUser(header);

        List<Alarm> uAl = em.createQuery("SELECT a FROM Alarm a where a.idUsr.idUsr = :idUsr",Alarm.class).setParameter("idUsr", user.getIdUsr()).getResultList();
        List<OldAlarm> alarms = new LinkedList<OldAlarm>();
        
        for (Alarm a : uAl) {
            em.refresh(a);
            alarms.add(new OldAlarm(a.getTime(),a.getPeriod(),a.getActive(), a.getIdAl()));
        }
        
        return Response.status(Response.Status.OK).entity(new GenericEntity<List<OldAlarm>>(alarms){}).build();
    }
    
    @PUT
    @Path("turnOff")
    @Consumes(MediaType.APPLICATION_XML)
    public Response turnOffAlarm(CreateAlarm al) {
        
        Alarm alarm = em.find(Alarm.class, al.getAlarmId());
        alarm.setActive((short)0);
        
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
