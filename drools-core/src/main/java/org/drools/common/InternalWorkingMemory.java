package org.drools.common;

import java.util.Map;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleFlowEventSupport;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.reteoo.LIANodePropagation;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;
import org.drools.util.ObjectHashMap;
import org.drools.util.concurrent.locks.Lock;

public interface InternalWorkingMemory
    extends
    WorkingMemory {      
    public long getId();
    
    public void setGlobals(Map globals);
    
    public void setWorkingMemoryEventSupport(WorkingMemoryEventSupport workingMemoryEventSupport);

    public void setAgendaEventSupport(AgendaEventSupport agendaEventSupport);

    public void setRuleFlowEventSupport(RuleFlowEventSupport ruleFlowEventSupport);

    public Object getNodeMemory(NodeMemory node);

    public void clearNodeMemory(NodeMemory node);

    public long getNextPropagationIdCounter();

    public ObjectHashMap getFactHandleMap();

    public TruthMaintenanceSystem getTruthMaintenanceSystem();

    public void executeQueuedActions();
    
    public void queueWorkingMemoryAction(final WorkingMemoryAction action);

    public FactHandleFactory getFactHandleFactory();

    public void removeLogicalDependencies(final Activation activation,
                                          final PropagationContext context,
                                          final Rule rule) throws FactException;
    
    void retract(final FactHandle factHandle,
                       final boolean removeLogical,
                       final boolean updateEqualsMap,
                       final Rule rule,
                       final Activation activation) throws FactException;

    public Lock getLock();    
    
    public boolean isSequential();
    
    public void addLIANodePropagation(LIANodePropagation liaNodePropagation);
}
