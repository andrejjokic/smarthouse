/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.planner;

import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import javax.swing.JLabel;
import smarthouse.entities.Event;
import smarthouse.entities.Location;
import smarthouse.entities.User;
import smarthouse.messagetypes.AlarmMessage;
import smarthouse.messagetypes.PlannerMessage;

/**
 *
 * @author Andrej
 */
public class Planner {

    private static Planner planner;
    private DistanceCalculator distanceCalc = new DistanceCalculator();
    
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connFactory;
    
    @Resource(lookup = "plannerQueue")
    private static Queue plannerQueue;
    
    @Resource(lookup = "alarmQueue")
    private static Queue alarmQueue;
    
    private Planner() {}
    
    public static Planner getPlanner() {
        if (planner == null)
            planner = new Planner();
        
        return planner;
    }
    
    private void addEvent(String name,Date start,int durationMin,int idLoc,int idUsr) {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Planner1PU");
        EntityManager em = emf.createEntityManager();
        
        Event event = new Event(0, name, start, durationMin);
        event.setIdUsr(em.find(User.class, idUsr));
        event.setIdLoc(em.find(Location.class, idLoc));

        if (canMakeIt(event)) {
            em.getTransaction().begin();
            em.persist(event);
            em.getTransaction().commit(); 
        }
        
        em.close();
        emf.close();
    }
    
    private Location getPrevLocation(Event event) {
        
        User user = event.getIdUsr();
        Event lastEvent = null;
        
        //Find last event
        for (Event e : user.getEventList()) {
            if ((lastEvent == null && e.getStart().getTime() < event.getStart().getTime()) || (lastEvent != null && lastEvent.getStart().getTime() < e.getStart().getTime() && e.getStart().getTime() < event.getStart().getTime()))
                lastEvent = e;
        }
        
        //Find location of user
        Location lastLocation = user.getIdLoc();
        if (lastEvent != null)
            lastLocation = lastEvent.getIdLoc();
        
        return lastLocation;
    }
    
    private boolean canMakeIt(Event event) {
        
        User user = event.getIdUsr();
        Calendar nowTime = Calendar.getInstance();
        
        //Can't make it because it starts before present time
        if (event.getStart().getTime() < nowTime.getTimeInMillis()) {
            new ErrorEvent("Error: Impossible event start time!");
            return false;
        }
        
        Event lastEvent = null;
        //Find last event
        for (Event e : user.getEventList()) {
            if ((lastEvent == null && e.getStart().getTime() < event.getStart().getTime()) || (lastEvent != null && lastEvent.getStart().getTime() < e.getStart().getTime() && e.getStart().getTime() < event.getStart().getTime()))
                lastEvent = e;
        }
        
        //Find location of user
        Location lastLocation = user.getIdLoc();
        if (lastEvent != null)
            lastLocation = lastEvent.getIdLoc();
        
        Calendar lastEventEndTime = Calendar.getInstance();
        //Find end time of last event if it ends after present time or present time otherwise
        if (lastEvent != null) {
            lastEventEndTime.setTime(lastEvent.getStart());
            lastEventEndTime.add(Calendar.MINUTE, lastEvent.getDuration());
            
            if (lastEventEndTime.getTimeInMillis() < nowTime.getTimeInMillis())
                lastEventEndTime.setTime(nowTime.getTime());
            
        } else {
            lastEventEndTime.setTime(nowTime.getTime());
        }
        
        Event nextEvent = null;     
        //Find next event
        for (Event e : user.getEventList()) {
            if ((nextEvent == null && e.getStart().getTime() > lastEventEndTime.getTimeInMillis()) || (nextEvent != null && e.getStart().getTime() > lastEventEndTime.getTimeInMillis() && e.getStart().getTime() < nextEvent.getStart().getTime()))
                nextEvent = e;
        }
        
        int travelTime = distanceCalc.calculateTime(lastLocation, event.getIdLoc());
        
        Calendar getThereTime = Calendar.getInstance();
        getThereTime.setTime(lastEventEndTime.getTime());
        getThereTime.add(Calendar.MINUTE, travelTime);
        
        Calendar endEventTime = Calendar.getInstance();
        endEventTime.setTime(event.getStart());
        endEventTime.add(Calendar.MINUTE, event.getDuration());
        
        if (getThereTime.getTimeInMillis() > event.getStart().getTime()) {
            new ErrorEvent("Error: You can't make it there before event starts!");
            return false;
        }
        
        if (nextEvent != null) {
            int travelTime2 = distanceCalc.calculateTime(event.getIdLoc(), nextEvent.getIdLoc());
            
            Calendar getToNextEventTime = Calendar.getInstance();
            getToNextEventTime.setTime(endEventTime.getTime());
            getToNextEventTime.add(Calendar.MINUTE, travelTime2);
            
            if (getToNextEventTime.getTimeInMillis() > nextEvent.getStart().getTime())  {
                new ErrorEvent("Error: You can't make it to the next event!");
                return false;
            }
        }
        
        return true;
    }
    
    private void changeEvent(PlannerMessage change) {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Planner1PU");
        EntityManager em = emf.createEntityManager();
        
        Event event = em.find(Event.class, change.getIdEv());
        
        if (event == null) {
            em.close();
            emf.close();
            return;
        }
        
        Event oldEvent = new Event(event.getIdEv(), event.getName(), event.getStart(), event.getDuration());
        oldEvent.setIdLoc(event.getIdLoc());
        oldEvent.setIdUsr(event.getIdUsr());
        
        Event newEvent = new Event(change.getIdEv(), change.getName(), change.getStart(), change.getDuration());
        newEvent.setIdUsr(event.getIdUsr());
        newEvent.setIdLoc(em.find(Location.class, change.getIdLoc()));
        
        em.getTransaction().begin();
        em.remove(event);
        em.getTransaction().commit();

        if (canMakeIt(newEvent)) {
            em.getTransaction().begin();
            em.persist(newEvent);
            em.getTransaction().commit();
        } else {
            em.getTransaction().begin();
            em.persist(oldEvent);
            em.getTransaction().commit();
        }
        
        em.close();
        emf.close();
    }
    
    private void setRemainder(int idEv) {
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Planner1PU");
        EntityManager em = emf.createEntityManager();
        
        Event event = em.find(Event.class, idEv);
        
        if (event == null)  return;
        
        Location prevLocation = getPrevLocation(event);
        
        int travelTime = distanceCalc.calculateTime(prevLocation, event.getIdLoc());
        
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTime(event.getStart());
        alarmTime.add(Calendar.MINUTE, -travelTime);
        
        if (alarmTime.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) return;
        
        AlarmMessage amsg = new AlarmMessage(event.getIdUsr().getIdUsr(), -1, alarmTime, 0);
        
        JMSContext context = connFactory.createContext();
        ObjectMessage msg = context.createObjectMessage(amsg);
        context.createProducer().send(alarmQueue, msg);
        
        em.close();
        emf.close();
    }
    
    public static void main(String[] args) {
        Planner planner = getPlanner();
        JMSConsumer consumer = connFactory.createContext().createConsumer(plannerQueue);
        
        consumer.setMessageListener(l -> {
            try {
                PlannerMessage msg = l.getBody(PlannerMessage.class);
                
                if (msg.getType().equals(PlannerMessage.Type.ADD_EVENT)) {
                    planner.addEvent(msg.getName(), msg.getStart(), msg.getDuration(), msg.getIdLoc(), msg.getIdUsr());
    
                } else if (msg.getType().equals(PlannerMessage.Type.CHANGE_EVENT)) {
                    planner.changeEvent(msg);
                    
                } else {
                    planner.setRemainder(msg.getIdEv());
                }
                
            } catch (JMSException ex) {
                Logger.getLogger(Planner.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        while (true);
    }
    
    private static class ErrorEvent extends JFrame {

        public ErrorEvent(String error){
            super("Error");
            setSize(700,700);
            add(new JLabel(error),BorderLayout.CENTER);
            setVisible(true);
        }

    }
}
