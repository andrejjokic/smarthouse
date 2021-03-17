/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.messagetypes;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Andrej
 */
public class PlannerMessage implements Serializable {
    
    public enum Type {ADD_EVENT, CHANGE_EVENT, SET_REMAINDER};
    
    private Type type;
    private String name;
    private Date start;
    private int duration;
    private int idLoc;
    private int idUsr;
    private int idEv;
    
    public PlannerMessage() {}

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public int getIdLoc() {
        return idLoc;
    }

    public void setIdLoc(int idLoc) {
        this.idLoc = idLoc;
    }

    public int getIdUsr() {
        return idUsr;
    }

    public void setIdUsr(int idUsr) {
        this.idUsr = idUsr;
    }

    public int getIdEv() {
        return idEv;
    }

    public void setIdEv(int idEv) {
        this.idEv = idEv;
    }
}
