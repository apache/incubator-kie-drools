package org.drools.impl;

import org.drools.SystemEventListener;
import org.drools.SystemEventListenerProvider;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.util.DelegatingSystemEventListener;

public class SystemEventListenerProviderImpl implements SystemEventListenerProvider{
    
    private DelegatingSystemEventListener    listener = new DelegatingSystemEventListener( new PrintStreamSystemEventListener() );
    
    public SystemEventListener getSystemEventListener() {
        return this.listener;
    }

    public void setSystemEventListener(SystemEventListener listener) {
        this.listener.setSystemEventListener( listener );
    }

}
