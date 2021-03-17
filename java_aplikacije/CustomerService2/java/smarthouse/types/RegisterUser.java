/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.types;

import java.io.Serializable;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Andrej
 */

@XmlRootElement
public class RegisterUser implements Serializable {
    private String name;
    private String username;
    private String password;
    private String songName;
    private String songUri;
    private double latitude;
    private double longitude;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public RegisterUser() {
        
    }
    
    public RegisterUser(String name, String username, String password, String songName, String songUri, double latitude, double longitude,String location) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.songName = songName;
        this.songUri = songUri;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
    }

    public String toXML() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(RegisterUser.class);
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUri() {
        return songUri;
    }

    public void setSongUri(String songUri) {
        this.songUri = songUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    } 
}
