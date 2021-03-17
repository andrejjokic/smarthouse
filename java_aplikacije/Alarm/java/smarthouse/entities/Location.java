/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Andrej
 */
@Entity
@Table(name = "location")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l"),
    @NamedQuery(name = "Location.findByIdLoc", query = "SELECT l FROM Location l WHERE l.idLoc = :idLoc"),
    @NamedQuery(name = "Location.findByName", query = "SELECT l FROM Location l WHERE l.name = :name"),
    @NamedQuery(name = "Location.findByLongitude", query = "SELECT l FROM Location l WHERE l.longitude = :longitude"),
    @NamedQuery(name = "Location.findByLatitude", query = "SELECT l FROM Location l WHERE l.latitude = :latitude")})
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdLoc")
    private Integer idLoc;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Longitude")
    private double longitude;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Latitude")
    private double latitude;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idLoc")
    private List<Event> eventList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idLoc")
    private List<User> userList;

    public Location() {
    }

    public Location(Integer idLoc) {
        this.idLoc = idLoc;
    }

    public Location(Integer idLoc, String name, double longitude, double latitude) {
        this.idLoc = idLoc;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Integer getIdLoc() {
        return idLoc;
    }

    public void setIdLoc(Integer idLoc) {
        this.idLoc = idLoc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @XmlTransient
    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    @XmlTransient
    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLoc != null ? idLoc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Location)) {
            return false;
        }
        Location other = (Location) object;
        if ((this.idLoc == null && other.idLoc != null) || (this.idLoc != null && !this.idLoc.equals(other.idLoc))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "smarthouse.entities.Location[ idLoc=" + idLoc + " ]";
    }
    
}
