package org.drools.common;

import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.event.AgendaEventSupport;
import org.drools.spi.FactHandleFactory;

public interface InternalWorkingMemory
    extends
    WorkingMemory {
    public long getId();

    public Object getNodeMemory(NodeMemory node);

    public void clearNodeMemory(NodeMemory node);

    public long getNextPropagationIdCounter();

    public Map getFactHandleMap();

    public AgendaEventSupport getAgendaEventSupport();

    public TruthMaintenanceSystem getTruthMaintenanceSystem();

    public void propagateQueuedActions();

    public FactHandleFactory getFactHandleFactory();
}
