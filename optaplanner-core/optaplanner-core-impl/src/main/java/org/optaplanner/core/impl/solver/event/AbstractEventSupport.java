package org.optaplanner.core.impl.solver.event;

import java.util.EventListener;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class AbstractEventSupport<E extends EventListener> {

    protected Set<E> eventListenerSet = new CopyOnWriteArraySet<>();

    public void addEventListener(E eventListener) {
        eventListenerSet.add(eventListener);
    }

    public void removeEventListener(E eventListener) {
        eventListenerSet.remove(eventListener);
    }

}
