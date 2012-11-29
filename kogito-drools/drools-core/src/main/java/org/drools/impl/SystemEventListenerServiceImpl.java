package org.drools.impl;

import org.drools.core.util.DelegatingSystemEventListener;
import org.kie.SystemEventListener;
import org.kie.SystemEventListenerService;

public class SystemEventListenerServiceImpl implements SystemEventListenerService{

    private DelegatingSystemEventListener listener = new DelegatingSystemEventListener( new DoNothingSystemEventListener() );

    public SystemEventListener getSystemEventListener() {
        return this.listener;
    }

    public void setSystemEventListener(SystemEventListener listener) {
        this.listener.setSystemEventListener( listener );
    }

    public static class DoNothingSystemEventListener
            implements
            SystemEventListener {

        public void debug(String message) {
        }

        public void debug(String message,
                          Object object) {
        }

        public void exception(String message, Throwable e) {
            // Doing nothing here would effectively eat the exception
            e.printStackTrace();
        }

        public void exception(Throwable e) {
            // Doing nothing here would effectively eat the exception
            e.printStackTrace();
        }

        public void info(String message) {
        }

        public void info(String message,
                         Object object) {
        }

        public void warning(String message) {
        }

        public void warning(String message,
                            Object object) {
        }
    }
}
