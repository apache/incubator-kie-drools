package org.drools.testframework;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.drools.Agenda;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.WorkingMemoryEntryPoint;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.ObjectStore;
import org.drools.common.ObjectTypeConfigurationRegistry;
import org.drools.common.RuleBasePartitionId;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.common.WorkingMemoryAction;
import org.drools.concurrent.ExecutorService;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.RuleFlowEventSupport;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.ProcessInstanceManager;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.event.SignalManager;
import org.drools.process.instance.timer.TimerManager;
import org.drools.reteoo.LIANodePropagation;
import org.drools.reteoo.PartitionTaskManager;
import org.drools.rule.Rule;
import org.drools.rule.TimeMachine;
import org.drools.runtime.Environment;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.spi.PropagationContext;
import org.drools.time.SessionClock;
import org.drools.time.TimerService;

public class MockWorkingMemory implements InternalWorkingMemory {
                
	List<Object> facts = new ArrayList<Object>();
	AgendaEventListener agendaEventListener;
	TimeMachine timeMachine = new TimeMachine();
	Map<String, Object> globals = new HashMap<String, Object>();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        facts   = (List<Object>)in.readObject();
        agendaEventListener   = (AgendaEventListener)in.readObject();
        timeMachine   = (TimeMachine)in.readObject();
        globals   = (Map<String, Object>)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(facts);
        out.writeObject(agendaEventListener);
        out.writeObject(timeMachine);
        out.writeObject(globals);
    }
	public Iterator iterateObjects() {
		return this.facts.iterator();
	}

	public void setGlobal(String identifier, Object value) {
		this.globals.put(identifier, value);

	}

	public void addEventListener(AgendaEventListener listener) {
		this.agendaEventListener = listener;
	}

	public void setTimeMachine(TimeMachine tm) {
		this.timeMachine = tm;

	}

    public void addLIANodePropagation(LIANodePropagation liaNodePropagation) {
        // TODO Auto-generated method stub
        
    }

    public void clearNodeMemory(NodeMemory node) {
        // TODO Auto-generated method stub
        
    }

    public void executeQueuedActions() {
        // TODO Auto-generated method stub
        
    }

    public ExecutorService getExecutorService() {
        // TODO Auto-generated method stub
        return null;
    }

    public FactHandle getFactHandleByIdentity(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public FactHandleFactory getFactHandleFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getId() {
        // TODO Auto-generated method stub
        return 0;
    }

    public InternalFactHandle getInitialFactHandle() {
        // TODO Auto-generated method stub
        return null;
    }

    public Lock getLock() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getNextPropagationIdCounter() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Object getNodeMemory(NodeMemory node) {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectStore getObjectStore() {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    public PartitionTaskManager getPartitionManager(RuleBasePartitionId partitionId) {
        // TODO Auto-generated method stub
        return null;
    }

    public ProcessInstanceManager getProcessInstanceManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public TimeMachine getTimeMachine() {
		return this.timeMachine;
    }

    public TimerService getTimerService() {
        // TODO Auto-generated method stub
        return null;
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isSequential() {
        // TODO Auto-generated method stub
        return false;
    }

    public void queueWorkingMemoryAction(WorkingMemoryAction action) {
        // TODO Auto-generated method stub
        
    }

    public void removeLogicalDependencies(Activation activation,
                                          PropagationContext context,
                                          Rule rule) throws FactException {
        // TODO Auto-generated method stub
        
    }

    public void removeProcessInstance(ProcessInstance processInstance) {
        // TODO Auto-generated method stub
        
    }

    public void retract(FactHandle factHandle,
                        boolean removeLogical,
                        boolean updateEqualsMap,
                        Rule rule,
                        Activation activation) throws FactException {
        // TODO Auto-generated method stub
        
    }

    public void setAgendaEventSupport(AgendaEventSupport agendaEventSupport) {
        // TODO Auto-generated method stub
        
    }

    public void setExecutorService(ExecutorService executor) {
        // TODO Auto-generated method stub
        
    }

    public void setId(int id) {
        // TODO Auto-generated method stub
        
    }

    public void setRuleBase(InternalRuleBase ruleBase) {
        // TODO Auto-generated method stub
        
    }

    public void setRuleFlowEventSupport(RuleFlowEventSupport ruleFlowEventSupport) {
        // TODO Auto-generated method stub
        
    }

    public void setWorkingMemoryEventSupport(WorkingMemoryEventSupport workingMemoryEventSupport) {
        // TODO Auto-generated method stub
        
    }

    public void clearActivationGroup(String group) {
        // TODO Auto-generated method stub
        
    }

    public void clearAgenda() {
        // TODO Auto-generated method stub
        
    }

    public void clearAgendaGroup(String group) {
        // TODO Auto-generated method stub
        
    }

    public void clearRuleFlowGroup(String group) {
        // TODO Auto-generated method stub
        
    }

    public int fireAllRules() throws FactException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int fireAllRules(AgendaFilter agendaFilter) throws FactException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int fireAllRules(int fireLimit) throws FactException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int fireAllRules(AgendaFilter agendaFilter,
                            int fireLimit) throws FactException {
        // TODO Auto-generated method stub
        return 0;
    }

    public Agenda getAgenda() {
        // TODO Auto-generated method stub
        return null;
    }

    public FactHandle getFactHandle(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getGlobal(String identifier) {
        // TODO Auto-generated method stub
        return null;
    }

    public GlobalResolver getGlobalResolver() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getObject(org.drools.runtime.rule.FactHandle handle) {
        // TODO Auto-generated method stub
        return null;
    }

    public ProcessInstance getProcessInstance(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<ProcessInstance> getProcessInstances() {
        // TODO Auto-generated method stub
        return null;
    }

    public QueryResults getQueryResults(String query) {
        // TODO Auto-generated method stub
        return null;
    }

    public QueryResults getQueryResults(String query,
                                        Object[] arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    public RuleBase getRuleBase() {
        // TODO Auto-generated method stub
        return null;
    }

    public SessionClock getSessionClock() {
        // TODO Auto-generated method stub
        return null;
    }

    public SignalManager getSignalManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public TimerManager getTimerManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public WorkItemManager getWorkItemManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    public void halt() {
        // TODO Auto-generated method stub
        
    }

    public Iterator< ? > iterateFactHandles() {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator< ? > iterateFactHandles(org.drools.runtime.ObjectFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator< ? > iterateObjects(org.drools.runtime.ObjectFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setAsyncExceptionHandler(AsyncExceptionHandler handler) {
        // TODO Auto-generated method stub
        
    }

    public void setFocus(String focus) {
        // TODO Auto-generated method stub
        
    }

    public void setGlobalResolver(GlobalResolver globalResolver) {
        // TODO Auto-generated method stub
        
    }

    public ProcessInstance startProcess(String processId) {
        // TODO Auto-generated method stub
        return null;
    }

    public ProcessInstance startProcess(String processId,
                                        Map<String, Object> parameters) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        // TODO Auto-generated method stub
        
    }

    public void addEventListener(RuleFlowEventListener listener) {
        // TODO Auto-generated method stub
        
    }

    public List getAgendaEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public List getRuleFlowEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public List getWorkingMemoryEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        // TODO Auto-generated method stub
        
    }

    public void removeEventListener(AgendaEventListener listener) {
        // TODO Auto-generated method stub
        
    }

    public void removeEventListener(RuleFlowEventListener listener) {
        // TODO Auto-generated method stub
        
    }

    public void addEventListener(RuleBaseEventListener listener) {
        // TODO Auto-generated method stub
        
    }

    public List<RuleBaseEventListener> getRuleBaseEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeEventListener(RuleBaseEventListener listener) {
        // TODO Auto-generated method stub
        
    }

    public FactHandle insert(Object object) throws FactException {
		this.facts .add(object);
		return new MockFactHandle(object.hashCode());
    }

    public FactHandle insert(Object object,
                             boolean dynamic) throws FactException {
        // TODO Auto-generated method stub
        return null;
    }

    public void modifyInsert(FactHandle factHandle,
                             Object object) {
        // TODO Auto-generated method stub
        
    }

    public void modifyRetract(FactHandle factHandle) {
        // TODO Auto-generated method stub
        
    }

    public void retract(org.drools.runtime.rule.FactHandle handle) throws FactException {
        // TODO Auto-generated method stub
        
    }

    public void update(org.drools.runtime.rule.FactHandle handle,
                       Object object) throws FactException {
        // TODO Auto-generated method stub
        
    }

    public KnowledgeRuntime getKnowledgeRuntime() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setKnowledgeRuntime(KnowledgeRuntime kruntime) {
        // TODO Auto-generated method stub
        
    }

	public Map<String, ExitPoint> getExitPoints() {
		// TODO Auto-generated method stub
		return null;
	}

    public Environment getEnvironment() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public SessionConfiguration getSessionConfiguration() {
        // TODO Auto-generated method stub
    	return null;
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    public void endBatchExecution() {
        // TODO Auto-generated method stub
        
    }

    public ExecutionResultImpl getExecutionResult() {
        // TODO Auto-generated method stub
        return null;
    }

    public void startBatchExecution(ExecutionResultImpl results) {
        // TODO Auto-generated method stub
        
    }

    public Collection< ? extends org.drools.runtime.rule.FactHandle> getFactHandles() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection< ? extends org.drools.runtime.rule.FactHandle> getFactHandles(ObjectFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection< Object > getObjects() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection< Object > getObjects(ObjectFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public void endOperation() {
        // TODO Auto-generated method stub
        
    }

    public long getIdleTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void startOperation() {
        // TODO Auto-generated method stub
        
    }

    public long getTimeToNextJob() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void updateEntryPointsCache() {
        // TODO Auto-generated method stub
        
    }

}
