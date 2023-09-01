package org.drools.base.time;
/**
 * An interface for Job Handles
 */
public interface JobHandle {
    
    long getId();

    void cancel();
    
    boolean isCancel();
}
