package org.drools.compiler.testframework;

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

import org.drools.core.Agenda;
import org.drools.core.FactException;
import org.drools.core.FactHandle;
import org.drools.core.QueryResults;
import org.drools.core.RuleBase;
import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.event.AgendaEventListener;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleBaseEventListener;
import org.drools.core.event.WorkingMemoryEventListener;
import org.drools.core.event.WorkingMemoryEventSupport;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LIANodePropagation;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.EntryPoint;
import org.drools.core.rule.Rule;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaFilter;
import org.drools.core.spi.AsyncExceptionHandler;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.TimerService;
import org.drools.core.type.DateFormats;
import org.kie.runtime.Calendars;
import org.kie.runtime.Channel;
import org.kie.runtime.Environment;
import org.kie.runtime.ObjectFilter;
import org.kie.runtime.process.ProcessInstance;
import org.kie.time.SessionClock;

public class MockWorkingMemory implements InternalWorkingMemory {
                
    List<Object> facts = new ArrayList<Object>();
    AgendaEventListener agendaEventListener;
    Map<String, Object> globals = new HashMap<String, Object>();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        facts   = (List<Object>)in.readObject();
        agendaEventListener   = (AgendaEventListener)in.readObject();
        globals   = (Map<String, Object>)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(facts);
        out.writeObject(agendaEventListener);
        out.writeObject(globals);
    }
    
    public Calendars getCalendars() {
        return null;
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

    public void addLIANodePropagation(LIANodePropagation liaNodePropagation) {
        // TODO Auto-generated method stub
        
    }

    public void clearNodeMemory(MemoryFactory node) {
        // TODO Auto-generated method stub
        
    }

    public void executeQueuedActions() {
        // TODO Auto-generated method stub
        
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

    public Memory getNodeMemory(MemoryFactory node) {
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

    public void delete(FactHandle factHandle,
                        Rule rule,
                        Activation activation) throws FactException {
        // TODO Auto-generated method stub
        
    }

    public void setAgendaEventSupport(AgendaEventSupport agendaEventSupport) {
        // TODO Auto-generated method stub
        
    }

    public void setId(int id) {
        // TODO Auto-generated method stub
        
    }

    public void setRuleBase(InternalRuleBase ruleBase) {
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

    public Object getObject(org.kie.runtime.rule.FactHandle handle) {
        // TODO Auto-generated method stub
        return null;
    }

    public ProcessInstance getProcessInstance(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public ProcessInstance getProcessInstance(long id, boolean readOnly) {
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

    public Iterator< ? > iterateFactHandles(org.kie.runtime.ObjectFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator< ? > iterateObjects(org.kie.runtime.ObjectFilter filter) {
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
    
    public InternalProcessRuntime getProcessRuntime() {
        // TODO Auto-generated method stub
        return null;
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
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

    public void retract(org.kie.runtime.rule.FactHandle handle) throws FactException {
        // TODO Auto-generated method stub
        
    }

    public void delete(org.kie.runtime.rule.FactHandle handle) throws FactException {
        // TODO Auto-generated method stub
    }

    public void update(org.kie.runtime.rule.FactHandle handle,
                       Object object) throws FactException {
        // TODO Auto-generated method stub
        
    }

    public InternalKnowledgeRuntime getKnowledgeRuntime() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime) {
        // TODO Auto-generated method stub
        
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

    public void activationFired() {
        // TODO Auto-generated method stub
        
    }

    public void prepareToFireActivation() {
        // TODO Auto-generated method stub
        
    }

    public String getEntryPointId() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getFactCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    public long getTotalFactCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    public DateFormats getDateFormats() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends org.kie.runtime.rule.FactHandle> Collection<T> getFactHandles() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends org.kie.runtime.rule.FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public EntryPoint getEntryPoint() {
        // TODO Auto-generated method stub
        return null;
    }

    public void insert(InternalFactHandle handle,
                       Object object,
                       Rule rule,
                       Activation activation,
                       ObjectTypeConf typeConf) {
        // TODO Auto-generated method stub
        
    }

    public Map<String, Channel> getChannels() {
        // TODO Auto-generated method stub
        return null;
    }

    public EntryPointNode getEntryPointNode() {
        // TODO Auto-generated method stub
        return null;
    }

    public void dispose() {
        // TODO Auto-generated method stub
    }

    public NodeMemories getNodeMemories() {
        // TODO Auto-generated method stub
        return null;
    }

}
