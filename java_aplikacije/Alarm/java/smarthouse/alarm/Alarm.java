/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.alarm;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import smarthouse.entities.User;
import smarthouse.messagetypes.AlarmMessage;
import smarthouse.messagetypes.PlayerMessage;

/**
 *
 * @author Andrej
 */
public class Alarm {

    private static Alarm alarm;
    
    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connFactory;
    
    @Resource(lookup = "alarmQueue")
    private static Queue alarmQueue;
    
    @Resource(lookup="playerQueue")
    private static Queue playerQueue;
    
    private Alarm() {}
    
    public static Alarm getAlarm() {
        if (alarm == null)
            alarm = new Alarm();
        
        return alarm;
    }
    
    private void setAlarm(int userId,int alarmId, Calendar time,int period) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmPU");
        EntityManager em = emf.createEntityManager();
        
        smarthouse.entities.Alarm al = null;
        
        if (alarmId == -1) {    //new alarm            
            al = new smarthouse.entities.Alarm();           
        } else {                //old alarm
            al = em.find(smarthouse.entities.Alarm.class, alarmId);
        }
        
        em.getTransaction().begin();
        
        al.setIdAl(alarmId);
        al.setIdUsr(em.find(User.class, userId));
        al.setPeriod(period);
        al.setTime(time.getTime());
        al.setActive((short)1);
       
        if (alarmId == -1)
            em.persist(al);
        
        em.getTransaction().commit();
                
        em.close();
        emf.close();
        
        //We need this to serve this alarm, because playAlarms() won't get this one, because it happened during execution
        new AlarmPlayer(al).start();
    }
    
    private void playAlarms() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmPU");
        EntityManager em = emf.createEntityManager();
        
        List<smarthouse.entities.Alarm> alarms = em.createQuery("SELECT a FROM Alarm a WHERE a.active = 1 AND (a.time > :time OR a.period > 0)",smarthouse.entities.Alarm.class)
                .setParameter("time", Calendar.getInstance().getTime()).getResultList();
               
        em.close();
        emf.close();
        
        for (smarthouse.entities.Alarm al : alarms) {
            new AlarmPlayer(al).start();
        }
    }
    
    public static void main(String[] args) {
        Alarm alarm = getAlarm();
        JMSConsumer consumer = connFactory.createContext().createConsumer(alarmQueue);
        
        //Thread that does messages
        consumer.setMessageListener(l -> {
            try {
                AlarmMessage msg = l.getBody(AlarmMessage.class);
                alarm.setAlarm(msg.getUserId(),msg.getAlarmId(), msg.getTime(), msg.getPeriod());
                
            } catch (JMSException ex) {
                Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        //We need this to serve all alarms set before application start
        alarm.playAlarms();
        
        while (true);
    }
    
    private class AlarmPlayer extends Thread {
        
        smarthouse.entities.Alarm alarm;
        
        public AlarmPlayer(smarthouse.entities.Alarm alarm) {
            this.alarm = alarm;
        }

        @Override
        public void run() {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("AlarmPU");
            EntityManager em = emf.createEntityManager();
            try {               
                while (true) {
                    long waitTime = alarm.getTime().getTime() - Calendar.getInstance().getTimeInMillis();

                    if (waitTime < 0 && alarm.getPeriod() > 0) {
                        Calendar c = Calendar.getInstance();
                        c.setTime(alarm.getTime());
                        c.add(Calendar.DAY_OF_YEAR, alarm.getPeriod());
                        
                        smarthouse.entities.Alarm ala = em.find(smarthouse.entities.Alarm.class, alarm.getIdAl());
                        em.getTransaction().begin();
                        ala.setTime(c.getTime());
                        em.getTransaction().commit();
                        continue;
                        
                    } else {
                        sleep(waitTime);

                        smarthouse.entities.Alarm ala = em.find(smarthouse.entities.Alarm.class, alarm.getIdAl());
                        if (ala.getActive() == 1) {
                            //User user = em.find(User.class, alarm.getIdUsr().getIdUsr());
                            User user = ala.getIdUsr();
                            em.refresh(user);
                            JMSContext context = connFactory.createContext();
                            ObjectMessage msg = context.createObjectMessage(new PlayerMessage(user.getIdUsr(), user.getAlarmSongUri(), user.getAlarmSongName(), PlayerMessage.Type.PLAY_SONG));
                            context.createProducer().send(playerQueue, msg);
                        }
                    }
                    
                    if (alarm.getPeriod() == 0) {
                        smarthouse.entities.Alarm ala = em.find(smarthouse.entities.Alarm.class, alarm.getIdAl());
                        em.getTransaction().begin();
                        ala.setActive((short)0);
                        em.getTransaction().commit();
                        break;
                    }
                    
                    Calendar c = Calendar.getInstance();
                    c.setTime(alarm.getTime());
                    c.add(Calendar.DAY_OF_YEAR, alarm.getPeriod());
       
                    smarthouse.entities.Alarm ala = em.find(smarthouse.entities.Alarm.class, alarm.getIdAl());
                    em.getTransaction().begin();
                    ala.setTime(c.getTime());
                    em.getTransaction().commit();
                }              
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Alarm.class.getName()).log(Level.SEVERE, null, ex);
                
            } finally {
                em.close();
                emf.close();
            }
        }
    }
}
