/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Andrej
 */
@Entity
@Table(name = "event")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
    @NamedQuery(name = "Event.findByIdEv", query = "SELECT e FROM Event e WHERE e.idEv = :idEv"),
    @NamedQuery(name = "Event.findByName", query = "SELECT e FROM Event e WHERE e.name = :name"),
    @NamedQuery(name = "Event.findByStart", query = "SELECT e FROM Event e WHERE e.start = :start"),
    @NamedQuery(name = "Event.findByDuration", query = "SELECT e FROM Event e WHERE e.duration = :duration")})
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdEv")
    private Integer idEv;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Duration")
    private int duration;
    @JoinColumn(name = "IdLoc", referencedColumnName = "IdLoc")
    @ManyToOne(optional = false)
    private Location idLoc;
    @JoinColumn(name = "IdUsr", referencedColumnName = "IdUsr")
    @ManyToOne(optional = false)
    private User idUsr;

    public Event() {
    }

    public Event(Integer idEv) {
        this.idEv = idEv;
    }

    public Event(Integer idEv, String name, Date start, int duration) {
        this.idEv = idEv;
        this.name = name;
        this.start = start;
        this.duration = duration;
    }

    public Integer getIdEv() {
        return idEv;
    }

    public void setIdEv(Integer idEv) {
        this.idEv = idEv;
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

    public Location getIdLoc() {
        return idLoc;
    }

    public void setIdLoc(Location idLoc) {
        this.idLoc = idLoc;
    }

    public User getIdUsr() {
        return idUsr;
    }

    public void setIdUsr(User idUsr) {
        this.idUsr = idUsr;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEv != null ? idEv.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.idEv == null && other.idEv != null) || (this.idEv != null && !this.idEv.equals(other.idEv))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "smarthouse.entities.Event[ idEv=" + idEv + " ]";
    }
    
}
