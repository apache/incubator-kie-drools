package org.drools.games.adventures;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.runtime.Channel;

public class UserSession {
    private int id;
    private Map<String, Channel> channels;
    
    private static AtomicInteger counter = new AtomicInteger(); 
    
    public UserSession() {
        this.id = counter.getAndIncrement();
        this.channels = new HashMap<String, Channel>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Map<String, Channel> getChannels() {
        return channels;
    }
    
    public void setChannels(Map<String, Channel> channels) {
        this.channels = channels;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        UserSession other = (UserSession) obj;
        if ( id != other.id ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserSession [id=" + id + ", channels=" + channels + "]";
    }

}
