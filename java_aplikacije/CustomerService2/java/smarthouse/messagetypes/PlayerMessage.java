/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse.messagetypes;

import java.io.Serializable;

/**
 *
 * @author Andrej
 */

public class PlayerMessage implements Serializable {
    
    public enum Type {PLAY_SONG,SHOW_PLAYED_SONGS};
    private int userId;
    private String uri;
    private String name;
    private Type type;

    public PlayerMessage() {
        
    }
    
    public PlayerMessage(int userId, String uri, String name, Type type) {
        this.userId = userId;
        this.uri = uri;
        this.name = name;
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
