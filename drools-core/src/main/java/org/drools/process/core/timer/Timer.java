package org.drools.process.core.timer;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class Timer {

    private long id;
    private long delay;
    private long period;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getDelay() {
        return delay;
    }
    
    public void setDelay(long delay) {
        this.delay = delay;
    }
    
    public long getPeriod() {
        return period;
    }
    
    public void setPeriod(long period) {
        this.period = period;
    }
    
    public String toString() {
    	String result =  "Timer";
    	if (delay != 0 || period != 0) {
    		result += "[";
    		if (delay != 0) {
    			result += "delay=" + delay;
    			if (period != 0) {
    				result += ", ";
    			}
    		}
    		if (period != 0) {
    			result += "period=" + period;
    		}
    		result += "]";
    	}
    	return result;
    }

}
