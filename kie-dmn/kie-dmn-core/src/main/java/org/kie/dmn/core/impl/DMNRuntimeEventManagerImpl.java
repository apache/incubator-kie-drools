package org.kie.dmn.core.impl;

import java.util.HashSet;
import java.util.Set;

import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;

public class DMNRuntimeEventManagerImpl implements DMNRuntimeEventManager {

    private Set<DMNRuntimeEventListener> listeners = new HashSet<>();

    private DMNRuntime dmnRuntime;

    public DMNRuntimeEventManagerImpl() {

    }

    public DMNRuntimeEventManagerImpl(DMNRuntime dmnRuntime) {
        this.dmnRuntime = dmnRuntime;
    }

    @Override
    public void addListener(DMNRuntimeEventListener listener) {
        if( listener != null ) {
            this.listeners.add( listener );
        }
    }

    @Override
    public void removeListener(DMNRuntimeEventListener listener) {
        this.listeners.remove( listener );
    }

    @Override
    public Set<DMNRuntimeEventListener> getListeners() {
        return listeners;
    }

    @Override
    public boolean hasListeners() {
        return !listeners.isEmpty();
    }

    @Override
    public DMNRuntime getRuntime() {
        return dmnRuntime;
    }

}
