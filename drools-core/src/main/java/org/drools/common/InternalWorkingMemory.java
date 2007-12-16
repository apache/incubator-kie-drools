package org.drools.common;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleFlowEventSupport;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.reteoo.LIANodePropagation;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.rule.TimeMachine;
import org.drools.ruleflow.common.instance.ProcessInstance;
import org.drools.spi.Activation;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;
import org.drools.util.ObjectHashMap;

public interface InternalWorkingMemory
    extends
    WorkingMemory {
    public long getId();
    
    public void setId(long id);
    
    void setRuleBase(final InternalRuleBase ruleBase);

    public void setWorkingMemoryEventSupport(WorkingMemoryEventSupport workingMemoryEventSupport);

    public ObjectHashMap getAssertMap();

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

	public TimeMachine getTimeMachine();

	public void setTimeMachine(TimeMachine tm);
    
    public void removeProcessInstance(ProcessInstance processInstance);
    
    /**
     * Returns the ObjectTypeConfiguration object for the given object
     * or creates a new one if none is found in the cache
     * 
     * @param object
     * @return
     */
    public ObjectTypeConf getObjectTypeConf(EntryPoint entryPoint, Object object);
    
    /**
     * Returns the Map<Object key, ObjectTypeConf conf> of object type
     * confs in this working memory
     *  
     * @return
     */
    public Map<Object, ObjectTypeConf> getObjectTypeConfMap(EntryPoint entryPoint);
}
