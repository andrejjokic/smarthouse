/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.types;

import java.io.StringWriter;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Andrej
 */

@XmlRootElement
public class ChangeEvent {
    
    private Date prevDate;
    private String name;
    private Date start;
    private int duration;
    private String locationName;
    private double locationLat;
    private double locationLon;

    public ChangeEvent() {}

    public ChangeEvent(Date prevDate, String name, Date start, int duration, String locationName, double locationLat, double locationLon) {
        this.prevDate = prevDate;
        this.name = name;
        this.start = start;
        this.duration = duration;
        this.locationName = locationName;
        this.locationLat = locationLat;
        this.locationLon = locationLon;
    }

    public String toXML() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ChangeEvent.class);
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
    
    public Date getPrevDate() {
        return prevDate;
    }

    public void setPrevDate(Date prevDate) {
        this.prevDate = prevDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLon() {
        return locationLon;
    }

    public void setLocationLon(double locationLon) {
        this.locationLon = locationLon;
    }  
}
