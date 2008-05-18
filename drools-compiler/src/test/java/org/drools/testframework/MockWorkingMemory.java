package org.drools.testframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.Agenda;
import org.drools.WorkingMemoryEntryPoint;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.ObjectFilter;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.ObjectStore;
import org.drools.common.ObjectTypeConfigurationRegistry;
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
import org.drools.process.instance.ProcessInstanceFactory;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.timer.TimerManager;
import org.drools.reteoo.LIANodePropagation;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.rule.TimeMachine;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.spi.PropagationContext;
import org.drools.util.ObjectHashMap;

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

    public void addLIANodePropagation(LIANodePropagation liaNodePropagation) {
		// TODO Auto-generated method stub

	}

	public void clearNodeMemory(NodeMemory node) {
		// TODO Auto-generated method stub

	}



	public void executeQueuedActions() {
		// TODO Auto-generated method stub

	}

	public ObjectHashMap getAssertMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public FactHandleFactory getFactHandleFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	public ObjectHashMap getFactHandleMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getId() {
		// TODO Auto-generated method stub
		return 0;
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

	public TimeMachine getTimeMachine() {
		return this.timeMachine;
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
			PropagationContext context, Rule rule) throws FactException {
		// TODO Auto-generated method stub

	}

	public void retract(FactHandle factHandle, boolean removeLogical,
			boolean updateEqualsMap, Rule rule, Activation activation)
			throws FactException {
		// TODO Auto-generated method stub

	}

	public void setAgendaEventSupport(AgendaEventSupport agendaEventSupport) {
		// TODO Auto-generated method stub

	}

	public void setRuleFlowEventSupport(
			RuleFlowEventSupport ruleFlowEventSupport) {
		// TODO Auto-generated method stub

	}

	public void setWorkingMemoryEventSupport(
			WorkingMemoryEventSupport workingMemoryEventSupport) {
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

	public void fireAllRules() throws FactException {
		// TODO Auto-generated method stub

	}

	public void fireAllRules(AgendaFilter agendaFilter) throws FactException {
		// TODO Auto-generated method stub

	}

	public void fireAllRules(int fireLimit) throws FactException {
		// TODO Auto-generated method stub

	}

	public void fireAllRules(AgendaFilter agendaFilter, int fireLimit)
			throws FactException {
		// TODO Auto-generated method stub

	}

	public Agenda getAgenda() {
		// TODO Auto-generated method stub
		return null;
	}

	public FactHandle getFactHandle(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	public AgendaGroup getFocus() {
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

	public Object getObject(FactHandle handle) {
		// TODO Auto-generated method stub
		return null;
	}

	public QueryResults getQueryResults(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	public QueryResults getQueryResults(String query, Object[] arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	public RuleBase getRuleBase() {
		return new MockRuleBase();
	}

	public void halt() {
		// TODO Auto-generated method stub

	}

	public FactHandle insert(Object object) throws FactException {
		this.facts .add(object);
		return new MockFactHandle(object.hashCode());
	}

	public FactHandle insert(Object object, long duration)
		throws FactException {
		// TODO Auto-generated method stub
		return null;
	}

	public FactHandle insert(Object object, boolean dynamic)
			throws FactException {
		// TODO Auto-generated method stub
		return null;
	}

	public FactHandle insert(Object object, long duration, boolean dynamic)
			throws FactException {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator iterateFactHandles() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator iterateFactHandles(ObjectFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator iterateObjects() {
		return this.facts.iterator();
	}

	public Iterator iterateObjects(ObjectFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void modifyInsert(FactHandle factHandle, Object object) {
		// TODO Auto-generated method stub

	}

	public void modifyRetract(FactHandle factHandle) {
		// TODO Auto-generated method stub

	}

	public void retract(FactHandle handle) throws FactException {
		// TODO Auto-generated method stub

	}

	public void setAsyncExceptionHandler(AsyncExceptionHandler handler) {
		// TODO Auto-generated method stub

	}

	public void setFocus(String focus) {
		// TODO Auto-generated method stub

	}

	public void setFocus(AgendaGroup focus) {
		// TODO Auto-generated method stub

	}

	public void setGlobal(String identifier, Object value) {
		this.globals.put(identifier, value);

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

	public void update(FactHandle handle, Object object) throws FactException {
		// TODO Auto-generated method stub

	}

	public void addEventListener(WorkingMemoryEventListener listener) {
		// TODO Auto-generated method stub

	}

	public void addEventListener(AgendaEventListener listener) {
		this.agendaEventListener = listener;
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

	public List getRuleBaseEventListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeEventListener(RuleBaseEventListener listener) {
		// TODO Auto-generated method stub

	}

	public void setTimeMachine(TimeMachine tm) {
		this.timeMachine = tm;

	}

    public void setId(long id) {
        // TODO Auto-generated method stub

    }

    public void setRuleBase(InternalRuleBase ruleBase) {
        // TODO Auto-generated method stub

    }

    public void addProcessInstance(ProcessInstance processInstance) {
        // TODO Auto-generated method stub
        
    }
    
    public void removeProcessInstance(ProcessInstance processInstance) {
        // TODO Auto-generated method stub

    }

    public ProcessInstance getProcessInstance(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection getProcessInstances() {
        // TODO Auto-generated method stub
        return null;
    }

    public WorkItemManager getWorkItemManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectTypeConf getObjectTypeConf(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<Object, ObjectTypeConf> getObjectTypeConfMap() {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectTypeConf getObjectTypeConf(EntryPoint entryPoint,
                                            Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<Object, ObjectTypeConf> getObjectTypeConfMap(EntryPoint entryPoint) {
        // TODO Auto-generated method stub
        return null;
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    public FactHandle getFactHandleByIdentity(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public void registerProcessInstanceFactory(String type,
            ProcessInstanceFactory nodeInstanceFactory) {
        // TODO Auto-generated method stub

    }

    public ObjectStore getObjectStore() {
        // TODO Auto-generated method stub
        return null;
    }

    public ExecutorService getExecutorService() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setExecutorService(ExecutorService executor) {
        // TODO Auto-generated method stub

    }

    public TimerManager getTimerManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    public InternalFactHandle getInitialFactHandle() {
        // TODO Auto-generated method stub
        return null;
    }

}
