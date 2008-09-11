package org.drools.process.instance;

public interface ProcessInstance  { 
	
    int STATE_PENDING   = 0;
    int STATE_ACTIVE    = 1;
    int STATE_COMPLETED = 2;
    int STATE_ABORTED   = 3;
    int STATE_SUSPENDED = 4;    
    
    String getProcessId();

    int getState();
    
    void signalEvent(String type, Object event);
    
}
