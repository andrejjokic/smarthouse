/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.messagetypes;

import java.io.Serializable;
import java.util.Calendar;

/**
 *
 * @author Andrej
 */

public class AlarmMessage implements Serializable {
    
    private int userId;
    private int alarmId;
    private Calendar time;
    private int period;

    public AlarmMessage(int userId,int alarmId, Calendar time, int period) {
        this.userId = userId;
        this.alarmId = alarmId;
        this.time = time;
        this.period = period;
    }

    public AlarmMessage() {}

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
}
