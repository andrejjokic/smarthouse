/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.entities;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Andrej
 */
@Entity
@Table(name = "songplayed")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Songplayed.findAll", query = "SELECT s FROM Songplayed s"),
    @NamedQuery(name = "Songplayed.findByIdUsr", query = "SELECT s FROM Songplayed s WHERE s.songplayedPK.idUsr = :idUsr"),
    @NamedQuery(name = "Songplayed.findBySong", query = "SELECT s FROM Songplayed s WHERE s.songplayedPK.song = :song")})
public class Songplayed implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SongplayedPK songplayedPK;

    public Songplayed() {
    }

    public Songplayed(SongplayedPK songplayedPK) {
        this.songplayedPK = songplayedPK;
    }

    public Songplayed(int idUsr, String song) {
        this.songplayedPK = new SongplayedPK(idUsr, song);
    }

    public SongplayedPK getSongplayedPK() {
        return songplayedPK;
    }

    public void setSongplayedPK(SongplayedPK songplayedPK) {
        this.songplayedPK = songplayedPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (songplayedPK != null ? songplayedPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Songplayed)) {
            return false;
        }
        Songplayed other = (Songplayed) object;
        if ((this.songplayedPK == null && other.songplayedPK != null) || (this.songplayedPK != null && !this.songplayedPK.equals(other.songplayedPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "smarthouse.entities.Songplayed[ songplayedPK=" + songplayedPK + " ]";
    }
    
}
