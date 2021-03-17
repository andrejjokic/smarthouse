/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.types;

import java.io.StringWriter;
import java.util.Calendar;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Andrej
 */

@XmlRootElement
public class CreateAlarm {
    
    private Calendar time;
    private int alarmId;
    private int period;
    private String user;

    public CreateAlarm() {}
    
    public CreateAlarm(Calendar time,int alarmId, int period, String user) {
        this.time = time;
        this.alarmId = alarmId;
        this.period = period;
        this.user = user;
    }
    
    public String toXML() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CreateAlarm.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(this, sw);
            
            return sw.toString();
            
        } catch(JAXBException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
    
}
