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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByIdUsr", query = "SELECT u FROM User u WHERE u.idUsr = :idUsr"),
    @NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE u.name = :name"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password"),
    @NamedQuery(name = "User.findByAlarmSongName", query = "SELECT u FROM User u WHERE u.alarmSongName = :alarmSongName"),
    @NamedQuery(name = "User.findByAlarmSongUri", query = "SELECT u FROM User u WHERE u.alarmSongUri = :alarmSongUri")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdUsr")
    private Integer idUsr;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Username")
    private String username;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "Password")
    private String password;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "AlarmSongName")
    private String alarmSongName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "AlarmSongUri")
    private String alarmSongUri;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsr")
    private List<Alarm> alarmList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idUsr")
    private List<Event> eventList;
    @JoinColumn(name = "IdLoc", referencedColumnName = "IdLoc")
    @ManyToOne(optional = false)
    private Location idLoc;

    public User() {
    }

    public User(Integer idUsr) {
        this.idUsr = idUsr;
    }

    public User(Integer idUsr, String name, String username, String password, String alarmSongName, String alarmSongUri) {
        this.idUsr = idUsr;
        this.name = name;
        this.username = username;
        this.password = password;
        this.alarmSongName = alarmSongName;
        this.alarmSongUri = alarmSongUri;
    }

    public Integer getIdUsr() {
        return idUsr;
    }

    public void setIdUsr(Integer idUsr) {
        this.idUsr = idUsr;
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

    public String getAlarmSongName() {
        return alarmSongName;
    }

    public void setAlarmSongName(String alarmSongName) {
        this.alarmSongName = alarmSongName;
    }

    public String getAlarmSongUri() {
        return alarmSongUri;
    }

    public void setAlarmSongUri(String alarmSongUri) {
        this.alarmSongUri = alarmSongUri;
    }

    @XmlTransient
    public List<Alarm> getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    @XmlTransient
    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public Location getIdLoc() {
        return idLoc;
    }

    public void setIdLoc(Location idLoc) {
        this.idLoc = idLoc;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUsr != null ? idUsr.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.idUsr == null && other.idUsr != null) || (this.idUsr != null && !this.idUsr.equals(other.idUsr))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "smarthouse.entities.User[ idUsr=" + idUsr + " ]";
    }
    
}
