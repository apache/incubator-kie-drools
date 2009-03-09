package org.drools.common;

import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.SessionConfiguration;
import org.drools.WorkingMemory;
import org.drools.concurrent.ExecutorService;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleFlowEventSupport;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.ProcessInstanceManager;
import org.drools.reteoo.LIANodePropagation;
import org.drools.reteoo.PartitionTaskManager;
import org.drools.rule.Rule;
import org.drools.rule.TimeMachine;
import org.drools.runtime.BatchExecutionResults;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.impl.BatchExecutionResultImpl;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.Activation;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;
import org.drools.time.TimerService;

public interface InternalWorkingMemory
    extends
    WorkingMemory {
    public int getId();
    
    public void setId(int id);
    
    void setRuleBase(final InternalRuleBase ruleBase);

    public void setWorkingMemoryEventSupport(WorkingMemoryEventSupport workingMemoryEventSupport);

    ///public ObjectHashMap getAssertMap();

    public void setAgendaEventSupport(AgendaEventSupport agendaEventSupport);

    public void setRuleFlowEventSupport(RuleFlowEventSupport ruleFlowEventSupport);
    
    public Object getNodeMemory(NodeMemory node);

    public void clearNodeMemory(NodeMemory node);

    public long getNextPropagationIdCounter();

    //public ObjectHashMap getFactHandleMap()
    
    public ObjectStore getObjectStore();

    public TruthMaintenanceSystem getTruthMaintenanceSystem();

    public void executeQueuedActions();

    public void queueWorkingMemoryAction(final WorkingMemoryAction action);

    public FactHandleFactory getFactHandleFactory();
    
    /**
     * Looks for the fact handle associated to the given object
     * by looking up the object IDENTITY (==), even if rule base
     * is configured to AssertBehavior.EQUALITY.
     * 
     * @param object
     * @return null if fact handle not found
     */
    public FactHandle getFactHandleByIdentity(final Object object);

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
    
    public ProcessInstanceManager getProcessInstanceManager();

    public ExecutorService getExecutorService();

    public void setExecutorService(ExecutorService executor);    
    
    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();
    
    public InternalFactHandle getInitialFactHandle();       
    
    /**
     * Returns the TimerService instance (session clock) for this
     * session.
     * 
     * @return
     */
    public TimerService getTimerService();

    /**
     * Returns the PartitionTaskManager for the given partition ID
     * in case the rulebase has partitions enabled
     *
     * @param partitionId the ID of the partition for which the task manager is assigned
     *
     * @return the PartitionTaskManager
     */
    public PartitionTaskManager getPartitionManager( RuleBasePartitionId partitionId );
    
    public void setKnowledgeRuntime(KnowledgeRuntime kruntime);
    
    public KnowledgeRuntime getKnowledgeRuntime();
    
    public Map<String, ExitPoint> getExitPoints();
    
    public Map<String, ? extends WorkingMemoryEntryPoint> getEntryPoints();

    public SessionConfiguration getSessionConfiguration();
    
    
    public void startBatchExecution();
    
    public BatchExecutionResultImpl getBatchExecutionResult();
    
    public void endBatchExecution();
}
