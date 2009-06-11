package org.drools.process.core.timer;

import java.io.Serializable;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class Timer implements Serializable {

    private long id;
    private String delay;
    private String period;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getDelay() {
        return delay;
    }
    
    public void setDelay(String delay) {
        this.delay = delay;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public String toString() {
    	String result =  "Timer";
    	if (delay != null || period != null) {
    		result += "[";
    		if (delay != null) {
    			result += "delay=" + delay;
    			if (period != null) {
    				result += ", ";
    			}
    		}
    		if (period != null) {
    			result += "period=" + period;
    		}
    		result += "]";
    	}
    	return result;
    }

}
