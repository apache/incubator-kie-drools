package org.drools.runtime;

import java.io.Serializable;

/**
 * This is a simple timer based expiring cache.
 * 
 * @author Michael Neale
 */
public class ExpiringResourceCopy implements Serializable {
    
    

    private static final long serialVersionUID = -1637341967721313804L;
    private final long timeOut;
    private byte[] data;
    private long startTime;
    
    public ExpiringResourceCopy(int seconds) {
        timeOut = seconds * 1000;
    }
    
    public void setData(byte[] data) {
        this.data = data;
        this.startTime = System.currentTimeMillis();
    }
    
    public boolean isExpired() {
        return (System.currentTimeMillis() - startTime) > timeOut;
    }
    
    public byte[] getData() {
        return data;
    }
    
    

}
