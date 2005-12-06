package org.drools.event;

import java.util.EventListener;

public interface ReteooNodeEventListener
    extends
    EventListener
{
    void nodeEvaluated(ReteooNodeEvent event);
}
