package org.drools.common;

import java.util.Map;

import org.drools.FactException;
import org.drools.WorkingMemory;
import org.drools.event.AgendaEventSupport;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;
import org.drools.util.ObjectHashMap;

public interface InternalWorkingMemory
    extends
    WorkingMemory {
    public long getId();

    public Object getNodeMemory(NodeMemory node);

    public void clearNodeMemory(NodeMemory node);

    public long getNextPropagationIdCounter();

    public ObjectHashMap getFactHandleMap();

    public AgendaEventSupport getAgendaEventSupport();

    public TruthMaintenanceSystem getTruthMaintenanceSystem();

    public void propagateQueuedActions();

    public FactHandleFactory getFactHandleFactory();
    
    public void removeLogicalDependencies(final Activation activation,
                                          final PropagationContext context,
                                          final Rule rule) throws FactException;
}
