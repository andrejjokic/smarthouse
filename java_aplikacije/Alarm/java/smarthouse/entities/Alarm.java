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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Andrej
 */
@Entity
@Table(name = "alarm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Alarm.findAll", query = "SELECT a FROM Alarm a"),
    @NamedQuery(name = "Alarm.findByIdAl", query = "SELECT a FROM Alarm a WHERE a.idAl = :idAl"),
    @NamedQuery(name = "Alarm.findByTime", query = "SELECT a FROM Alarm a WHERE a.time = :time"),
    @NamedQuery(name = "Alarm.findByPeriod", query = "SELECT a FROM Alarm a WHERE a.period = :period"),
    @NamedQuery(name = "Alarm.findByActive", query = "SELECT a FROM Alarm a WHERE a.active = :active")})
public class Alarm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IdAl")
    private Integer idAl;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
    @Column(name = "Period")
    private Integer period;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Active")
    private short active;
    @JoinColumn(name = "IdUsr", referencedColumnName = "IdUsr")
    @ManyToOne(optional = false)
    private User idUsr;

    public Alarm() {
    }

    public Alarm(Integer idAl) {
        this.idAl = idAl;
    }

    public Alarm(Integer idAl, Date time, short active) {
        this.idAl = idAl;
        this.time = time;
        this.active = active;
    }

    public Integer getIdAl() {
        return idAl;
    }

    public void setIdAl(Integer idAl) {
        this.idAl = idAl;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public short getActive() {
        return active;
    }

    public void setActive(short active) {
        this.active = active;
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
        hash += (idAl != null ? idAl.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Alarm)) {
            return false;
        }
        Alarm other = (Alarm) object;
        if ((this.idAl == null && other.idAl != null) || (this.idAl != null && !this.idAl.equals(other.idAl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "smarthouse.entities.Alarm[ idAl=" + idAl + " ]";
    }
    
}
