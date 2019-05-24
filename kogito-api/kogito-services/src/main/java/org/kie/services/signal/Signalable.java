package org.kie.services.signal;

public interface Signalable {

    public void signalEvent(String type, Object event);
}
