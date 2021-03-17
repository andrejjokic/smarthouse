/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.types;

import java.util.Date;

/**
 *
 * @author Andrej
 */
public class OldAlarm {
    
    private Date time;
    private int period;
    private short active;
    private int alarmId;
    
    public OldAlarm() {
        
    }

    public OldAlarm(Date time,int period,short active, int alarmId) {
        this.active = active;
        this.period = period;
        this.time = time;
        this.alarmId = alarmId;
    }

    public short getActive() {
        return active;
    }

    public void setActive(short active) {
        this.active = active;
    }
    
    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
    
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }
}
