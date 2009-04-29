package org.drools.runtime.process;

public interface ProcessInstance
    extends
    EventListener {

    int STATE_PENDING   = 0;
    int STATE_ACTIVE    = 1;
    int STATE_COMPLETED = 2;
    int STATE_ABORTED   = 3;
    int STATE_SUSPENDED = 4;

    String getProcessId();

    long getId();

    String getProcessName();

    int getState();
    

    void signalEvent(String type, 
                     Object event);
}
