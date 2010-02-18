package org.drools.core.util;

import org.drools.SystemEventListener;

public class DelegatingSystemEventListener
        implements
        SystemEventListener {

    private SystemEventListener listener;

    public DelegatingSystemEventListener(SystemEventListener listener) {
        this.listener = listener;
    }

    public void setSystemEventListener(SystemEventListener listener) {
        this.listener = listener;
    }

    public void debug(String message) {
        this.listener.debug(message);
    }

    public void debug(String message,
                      Object object) {
        this.listener.debug(message, object);
    }

    public void exception(String message, Throwable e) {
        this.listener.exception(message, e);
    }

    public void exception(Throwable e) {
        this.listener.exception(e);
    }

    public void info(String message) {
        this.listener.info(message);
    }

    public void info(String message,
                     Object object) {
        this.listener.info(message, object);
    }

    public void warning(String message) {
        this.listener.warning(message);
    }

    public void warning(String message,
                        Object object) {
        this.listener.warning(message, object);
    }

}
