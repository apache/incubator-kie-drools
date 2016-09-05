package org.drools.core.base;

import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultAgenda.ExecutionState;
import org.drools.core.common.EndOperationListener;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.EntryPointId;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AsyncExceptionHandler;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.time.TimerService;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.command.Command;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.FactHandle.State;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.command.Context;
import org.kie.internal.event.rule.RuleEventListener;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.KnowledgeRuntime;
import org.kie.internal.runtime.beliefs.Mode;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

/**
 * Wrapper of StatefulKnowledgeSessionImpl so to intercept call from RHS internal Drools execution and proxy or delegate method call as appropriate.
 */
public final class WrappedStatefulKnowledgeSessionForRHS
		implements KieSession, InternalWorkingMemoryActions, EventSupport, KnowledgeRuntime, Externalizable {
	private StatefulKnowledgeSessionImpl delegate;

	public WrappedStatefulKnowledgeSessionForRHS(WorkingMemory workingMemory) {
		super();
		this.delegate = (StatefulKnowledgeSessionImpl) workingMemory;
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		delegate = (StatefulKnowledgeSessionImpl) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject( delegate );
	}
	
	@Override
	public QueryResults getQueryResults(String queryName, Object... arguments) {
		return delegate.getQueryResultsFromRHS(queryName, arguments);
	}
	
	// -- then just delegate
	
	public KieRuntimeLogger getLogger() {
		return delegate.getLogger();
	}

	public void setLogger(KieRuntimeLogger logger) {
		delegate.setLogger(logger);
	}

	public <T> T getKieRuntime(Class<T> cls) {
		return delegate.getKieRuntime(cls);
	}

	public <T> T createRuntimeService(Class<T> cls) {
		return delegate.createRuntimeService(cls);
	}

	public Map<String, WorkingMemoryEntryPoint> getEntryPointMap() {
		return delegate.getEntryPointMap();
	}
	
	public void addEventListener(ProcessEventListener listener) {
		delegate.addEventListener(listener);
	}

	public Collection<ProcessEventListener> getProcessEventListeners() {
		return delegate.getProcessEventListeners();
	}

	public void removeEventListener(ProcessEventListener listener) {
		delegate.removeEventListener(listener);
	}

	public KnowledgeBase getKieBase() {
		return delegate.getKieBase();
	}

	public boolean isAlive() {
		return delegate.isAlive();
	}

	public void destroy() {
		delegate.destroy();
	}

	public void update(FactHandle factHandle) {
		delegate.update(factHandle);
	}

	public void abortProcessInstance(long id) {
		delegate.abortProcessInstance(id);
	}

	public void signalEvent(String type, Object event) {
		delegate.signalEvent(type, event);
	}

	public void signalEvent(String type, Object event, long processInstanceId) {
		delegate.signalEvent(type, event, processInstanceId);
	}

	public Globals getGlobals() {
		return delegate.getGlobals();
	}

	public <T> T execute(Command<T> command) {
		return delegate.execute(command);
	}

	public <T> T execute(Context context, Command<T> command) {
		return delegate.execute(context, command);
	}

	public void initInitialFact(InternalKnowledgeBase kBase, MarshallerReaderContext context) {
		delegate.initInitialFact(kBase, context);
	}

	public LiveQuery openLiveQuery(String query, Object[] arguments, ViewChangedEventListener listener) {
		return delegate.openLiveQuery(query, arguments, listener);
	}

	public void reset(int handleId, long handleCounter, long propagationCounter) {
		delegate.reset(handleId, handleCounter, propagationCounter);
	}

	public void addEventListener(RuleEventListener listener) {
		delegate.addEventListener(listener);
	}

	public void removeEventListener(RuleEventListener listener) {
		delegate.removeEventListener(listener);
	}

	public int getId() {
		return delegate.getId();
	}

	public void fireUntilHalt() {
		delegate.fireUntilHalt();
	}

	public void fireUntilHalt(AgendaFilter agendaFilter) {
		delegate.fireUntilHalt(agendaFilter);
	}

	public void executeQueuedActions() {
		delegate.executeQueuedActions();
	}

	public RuleRuntimeEventSupport getRuleRuntimeEventSupport() {
		return delegate.getRuleRuntimeEventSupport();
	}

	public AgendaEventSupport getAgendaEventSupport() {
		return delegate.getAgendaEventSupport();
	}

	public long getPropagationIdCounter() {
		return delegate.getPropagationIdCounter();
	}

	public ProcessInstance createProcessInstance(String processId, Map<String, Object> parameters) {
		return delegate.createProcessInstance(processId, parameters);
	}

	public ProcessInstance startProcessInstance(long processInstanceId) {
		return delegate.startProcessInstance(processInstanceId);
	}

	public ProcessInstance createProcessInstance(String processId, CorrelationKey correlationKey,
			Map<String, Object> parameters) {
		return delegate.createProcessInstance(processId, correlationKey, parameters);
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public ProcessInstance getProcessInstance(CorrelationKey correlationKey) {
		return delegate.getProcessInstance(correlationKey);
	}

	public Map<String, WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
		return delegate.getWorkingMemoryEntryPoints();
	}

	public long getLastIdleTimestamp() {
		return delegate.getLastIdleTimestamp();
	}

	public Entry[] getActivationParameters(long activationId) {
		return delegate.getActivationParameters(activationId);
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public ProcessInstance startProcess(String processId, CorrelationKey correlationKey,
			Map<String, Object> parameters) {
		return delegate.startProcess(processId, correlationKey, parameters);
	}

	public void registerChannel(String name, Channel channel) {
		delegate.registerChannel(name, channel);
	}

	public void unregisterChannel(String name) {
		delegate.unregisterChannel(name);
	}
	
	public void setEndOperationListener(EndOperationListener listener) {
		delegate.setEndOperationListener(listener);
	}

	public String toString() {
		return delegate.toString();
	}

	public List iterateObjectsToList() {
		return delegate.iterateObjectsToList();
	}

	public List iterateNonDefaultEntryPointObjectsToList() {
		return delegate.iterateNonDefaultEntryPointObjectsToList();
	}
	
	public Map getActivationParameters(Activation activation) {
		return delegate.getActivationParameters(activation);
	}

	public void addEventListener(KieBaseEventListener listener) {
		delegate.addEventListener(listener);
	}

	public FactHandle insert(Object object) {
		return delegate.insert(object);
	}

	public void submit( AtomicAction action ) {
		delegate.submit( action );
	}

	public void removeEventListener(KieBaseEventListener listener) {
		delegate.removeEventListener(listener);
	}

	public void addEventListener(RuleRuntimeEventListener listener) {
		delegate.addEventListener(listener);
	}

	public Collection<KieBaseEventListener> getKieBaseEventListeners() {
		return delegate.getKieBaseEventListeners();
	}

	public FactHandle insert(Object object, boolean dynamic) {
		return delegate.insert(object, dynamic);
	}

	public void update(FactHandle handle, Object object, BitMask mask, Class<?> modifiedClass, Activation activation) {
		delegate.update(handle, object, mask, modifiedClass, activation);
	}

	public void removeEventListener(RuleRuntimeEventListener listener) {
		delegate.removeEventListener(listener);
	}

	public void delete(FactHandle factHandle, RuleImpl rule, Activation activation, State fhState) {
		delegate.delete(factHandle, rule, activation, fhState);
	}

	public void delete(FactHandle handle, RuleImpl rule, Activation activation) {
		delegate.delete(handle, rule, activation);
	}

	public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
		return delegate.getRuleRuntimeEventListeners();
	}

	public FactHandle insert(Object object, Object value, boolean dynamic, boolean logical, RuleImpl rule,
			Activation activation) {
		return delegate.insert(object, value, dynamic, logical, rule, activation);
	}

	public void addEventListener(AgendaEventListener listener) {
		delegate.addEventListener(listener);
	}

	public String getEntryPointId() {
		return delegate.getEntryPointId();
	}

	public void retract(FactHandle handle) {
		delegate.retract(handle);
	}

	public TruthMaintenanceSystem getTruthMaintenanceSystem() {
		return delegate.getTruthMaintenanceSystem();
	}

	public InternalAgenda getAgenda() {
		return delegate.getAgenda();
	}

	public long getIdentifier() {
		return delegate.getIdentifier();
	}

	public void setIdentifier(long id) {
		delegate.setIdentifier(id);
	}

	public InternalWorkingMemory getInternalWorkingMemory() {
		return delegate.getInternalWorkingMemory();
	}

	public void setRuleRuntimeEventSupport(RuleRuntimeEventSupport workingMemoryEventSupport) {
		delegate.setRuleRuntimeEventSupport(workingMemoryEventSupport);
	}

	public void removeEventListener(AgendaEventListener listener) {
		delegate.removeEventListener(listener);
	}

	public void updateTraits(InternalFactHandle h, BitMask mask, Class<?> modifiedClass, Activation activation) {
		delegate.updateTraits(h, mask, modifiedClass, activation);
	}

	public void update(FactHandle handle, Object object) {
		delegate.update(handle, object);
	}

	public void update(FactHandle handle, Object object, String... modifiedProperties) {
		delegate.update(handle, object, modifiedProperties);
	}

	public void setAgendaEventSupport(AgendaEventSupport agendaEventSupport) {
		delegate.setAgendaEventSupport(agendaEventSupport);
	}

	public void setGlobal(String identifier, Object value) {
		delegate.setGlobal(identifier, value);
	}

	public void reset() {
		delegate.reset();
	}

	public <T, K, X extends TraitableBean> Thing<K> shed(Activation activation, TraitableBean<K, X> core,
			Class<T> trait) {
		return delegate.shed(activation, core, trait);
	}

	public <T extends Memory> T getNodeMemory(MemoryFactory<T> node) {
		return delegate.getNodeMemory(node);
	}

	public FactHandleFactory getHandleFactory() {
		return delegate.getHandleFactory();
	}

	public Collection<AgendaEventListener> getAgendaEventListeners() {
		return delegate.getAgendaEventListeners();
	}

	public void clearNodeMemory(MemoryFactory node) {
		delegate.clearNodeMemory(node);
	}

	public void removeFromObjectStore(InternalFactHandle handle) {
		delegate.removeFromObjectStore(handle);
	}

	public <T, K> T don(Activation activation, K core, Collection<Class<? extends Thing>> traits, boolean b,
			Mode[] modes) {
		return delegate.don(activation, core, traits, b, modes);
	}

	public NodeMemories getNodeMemories() {
		return delegate.getNodeMemories();
	}

	public long getNextPropagationIdCounter() {
		return delegate.getNextPropagationIdCounter();
	}

	public ObjectStore getObjectStore() {
		return delegate.getObjectStore();
	}

	public <T, K> T don(Activation activation, K core, Class<T> trait, boolean b, Mode[] modes) {
		return delegate.don(activation, core, trait, b, modes);
	}

	public void executeQueuedActionsForRete() {
		delegate.executeQueuedActionsForRete();
	}

	public void queueWorkingMemoryAction(WorkingMemoryAction action) {
		delegate.queueWorkingMemoryAction(action);
	}

	public void delete(FactHandle handle) {
		delegate.delete(handle);
	}

	public void dispose() {
		delegate.dispose();
	}

	public FactHandleFactory getFactHandleFactory() {
		return delegate.getFactHandleFactory();
	}

	public Object getGlobal(String identifier) {
		return delegate.getGlobal(identifier);
	}

	public EntryPointId getEntryPoint() {
		return delegate.getEntryPoint();
	}

	public EntryPointNode getEntryPointNode() {
		return delegate.getEntryPointNode();
	}

	public EntryPoint getEntryPoint(String name) {
		return delegate.getEntryPoint(name);
	}

	public void insert(InternalFactHandle handle, Object object, RuleImpl rule, Activation activation,
			ObjectTypeConf typeConf) {
		delegate.insert(handle, object, rule, activation, typeConf);
	}

	public void delete(FactHandle handle, State fhState) {
		delegate.delete(handle, fhState);
	}

	public Environment getEnvironment() {
		return delegate.getEnvironment();
	}

	public void setGlobalResolver(GlobalResolver globalResolver) {
		delegate.setGlobalResolver(globalResolver);
	}

	public FactHandle getFactHandleByIdentity(Object object) {
		return delegate.getFactHandleByIdentity(object);
	}

	public GlobalResolver getGlobalResolver() {
		return delegate.getGlobalResolver();
	}

	public InternalKnowledgeBase getKnowledgeBase() {
		return delegate.getKnowledgeBase();
	}

	public Lock getLock() {
		return delegate.getLock();
	}

	public boolean isSequential() {
		return delegate.isSequential();
	}

	public int fireAllRules() {
		return delegate.fireAllRules();
	}

	public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
		return delegate.getObjectTypeConfigurationRegistry();
	}

	public InternalFactHandle getInitialFactHandle() {
		return delegate.getInitialFactHandle();
	}

	public int fireAllRules(AgendaFilter agendaFilter) {
		return delegate.fireAllRules(agendaFilter);
	}

	public Calendars getCalendars() {
		return delegate.getCalendars();
	}

	public TimerService getTimerService() {
		return delegate.getTimerService();
	}

	public int fireAllRules(int fireLimit) {
		return delegate.fireAllRules(fireLimit);
	}

	public InternalKnowledgeRuntime getKnowledgeRuntime() {
		return delegate.getKnowledgeRuntime();
	}

	public int fireAllRules(AgendaFilter agendaFilter, int fireLimit) {
		return delegate.fireAllRules(agendaFilter, fireLimit);
	}

	public Map<String, Channel> getChannels() {
		return delegate.getChannels();
	}

	public Collection<? extends EntryPoint> getEntryPoints() {
		return delegate.getEntryPoints();
	}

	public Object getObject(FactHandle handle) {
		return delegate.getObject(handle);
	}

	public SessionConfiguration getSessionConfiguration() {
		return delegate.getSessionConfiguration();
	}

	public void startBatchExecution(ExecutionResultImpl results) {
		delegate.startBatchExecution(results);
	}

	public ExecutionResultImpl getExecutionResult() {
		return delegate.getExecutionResult();
	}

	public Collection<? extends Object> getObjects() {
		return delegate.getObjects();
	}

	public void endBatchExecution() {
		delegate.endBatchExecution();
	}

	public void startOperation() {
		delegate.startOperation();
	}

	public FactHandle getFactHandle(Object object) {
		return delegate.getFactHandle(object);
	}

	public Iterator<?> iterateObjects() {
		return delegate.iterateObjects();
	}

	public void endOperation() {
		delegate.endOperation();
	}

	public Iterator<?> iterateObjects(ObjectFilter filter) {
		return delegate.iterateObjects(filter);
	}

	public Collection<? extends Object> getObjects(ObjectFilter filter) {
		return delegate.getObjects(filter);
	}

	public long getIdleTime() {
		return delegate.getIdleTime();
	}

	public <T extends FactHandle> Collection<T> getFactHandles() {
		return delegate.getFactHandles();
	}

	public long getTimeToNextJob() {
		return delegate.getTimeToNextJob();
	}

	public Iterator<InternalFactHandle> iterateFactHandles() {
		return delegate.iterateFactHandles();
	}

	public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
		return delegate.getFactHandles(filter);
	}

	public void updateEntryPointsCache() {
		delegate.updateEntryPointsCache();
	}

	public void prepareToFireActivation() {
		delegate.prepareToFireActivation();
	}

	public Iterator<InternalFactHandle> iterateFactHandles(ObjectFilter filter) {
		return delegate.iterateFactHandles(filter);
	}

	public long getFactCount() {
		return delegate.getFactCount();
	}

	public void activationFired() {
		delegate.activationFired();
	}

	public long getTotalFactCount() {
		return delegate.getTotalFactCount();
	}

	public void setFocus(String focus) {
		delegate.setFocus(focus);
	}

	public InternalProcessRuntime getProcessRuntime() {
		return delegate.getProcessRuntime();
	}

	public InternalProcessRuntime internalGetProcessRuntime() {
		return delegate.internalGetProcessRuntime();
	}

	public void closeLiveQuery(InternalFactHandle factHandle) {
		delegate.closeLiveQuery(factHandle);
	}

	public void addPropagation(PropagationEntry propagationEntry) {
		delegate.addPropagation(propagationEntry);
	}

	public void flushPropagations() {
		delegate.flushPropagations();
	}

	public void flushPropagations(PropagationEntry propagationEntry) {
		delegate.flushPropagations(propagationEntry);
	}

	public void flushNonMarshallablePropagations() {
		delegate.flushNonMarshallablePropagations();
	}

	public void activate() {
		delegate.activate();
	}

	public void deactivate() {
		delegate.deactivate();
	}

	public boolean tryDeactivate() {
		return delegate.tryDeactivate();
	}

	public void notifyEngineInactive() {
		delegate.notifyEngineInactive();
	}

	public void setAsyncExceptionHandler(AsyncExceptionHandler handler) {
		delegate.setAsyncExceptionHandler(handler);
	}

	public boolean hasPendingPropagations() {
		return delegate.hasPendingPropagations();
	}

	public PropagationEntry takeAllPropagations() {
		return delegate.takeAllPropagations();
	}

	public Iterator<? extends PropagationEntry> getActionsIterator() {
		return delegate.getActionsIterator();
	}

	public void removeGlobal(String identifier) {
		delegate.removeGlobal(identifier);
	}

	public void notifyWaitOnRest() {
		delegate.notifyWaitOnRest();
	}

	public void clearAgenda() {
		delegate.clearAgenda();
	}

	public PropagationEntry handleRestOnFireUntilHalt(ExecutionState currentState) {
		return delegate.handleRestOnFireUntilHalt(currentState);
	}

	public void clearAgendaGroup(String group) {
		delegate.clearAgendaGroup(group);
	}

	public void clearActivationGroup(String group) {
		delegate.clearActivationGroup(group);
	}

	public void clearRuleFlowGroup(String group) {
		delegate.clearRuleFlowGroup(group);
	}

	public ProcessInstance startProcess(String processId) {
		return delegate.startProcess(processId);
	}

	public ProcessInstance startProcess(String processId, Map<String, Object> parameters) {
		return delegate.startProcess(processId, parameters);
	}

	public Collection<ProcessInstance> getProcessInstances() {
		return delegate.getProcessInstances();
	}

	public ProcessInstance getProcessInstance(long id) {
		return delegate.getProcessInstance(id);
	}

	public ProcessInstance getProcessInstance(long id, boolean readOnly) {
		return delegate.getProcessInstance(id, readOnly);
	}

	public WorkItemManager getWorkItemManager() {
		return delegate.getWorkItemManager();
	}

	public void halt() {
		delegate.halt();
	}

	public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String id) {
		return delegate.getWorkingMemoryEntryPoint(id);
	}

	public SessionClock getSessionClock() {
		return delegate.getSessionClock();
	}	
}