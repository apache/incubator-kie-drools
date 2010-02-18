package org.drools.impl;

import org.drools.SystemEventListener;
import org.drools.SystemEventListenerService;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.core.util.DelegatingSystemEventListener;

public class SystemEventListenerServiceImpl implements SystemEventListenerService{
    
    private DelegatingSystemEventListener    listener = new DelegatingSystemEventListener( new PrintStreamSystemEventListener() );
    
    public SystemEventListener getSystemEventListener() {
        return this.listener;
    }

    public void setSystemEventListener(SystemEventListener listener) {
        this.listener.setSystemEventListener( listener );
    }

}
