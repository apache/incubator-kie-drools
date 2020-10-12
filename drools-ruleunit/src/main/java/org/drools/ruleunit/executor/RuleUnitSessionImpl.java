/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ruleunit.executor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.drools.core.QueryResultsImpl;
import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.EndOperationListener;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.AbstractRuntime;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.PropagationList;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AsyncExceptionHandler;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.time.TimerService;
import org.drools.core.util.bitmask.BitMask;
import org.drools.ruleunit.impl.RuleUnitKnowledgeHelper;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessEventManager;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.internal.runtime.beliefs.Mode;
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
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;
import org.kie.internal.event.rule.RuleEventListener;
import org.kie.internal.event.rule.RuleEventManager;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class RuleUnitSessionImpl extends AbstractRuntime
        implements
        StatefulKnowledgeSession,
        WorkingMemoryEntryPoint,
        InternalKnowledgeRuntime,
        KieSession,
        KieRuntimeEventManager,
        InternalWorkingMemoryActions,
        EventSupport,
        RuleEventManager,
        ProcessEventManager,
        CorrelationAwareProcessRuntime,
        Externalizable {

    private final StatefulKnowledgeSessionImpl session;

    protected transient InternalRuleUnitExecutor ruleUnitExecutor;

    public RuleUnitSessionImpl(InternalRuleUnitExecutor ruleUnitExecutor, StatefulKnowledgeSessionImpl session) {
        this.session = session;
        this.ruleUnitExecutor = ruleUnitExecutor;
    }

    public StatefulKnowledgeSessionImpl getSession() {
        return session;
    }

    @Override
    public KnowledgeHelper createKnowledgeHelper() {
        return new RuleUnitKnowledgeHelper( this );
    }

    @Override
    public KnowledgeHelper createKnowledgeHelper(Activation activation) {
        return new RuleUnitKnowledgeHelper( activation, this );
    }

    public void init(SessionConfiguration config, Environment environment) {
        session.init( config, environment );
    }

    public void bindRuleBase( InternalKnowledgeBase kBase, InternalAgenda agenda, boolean initInitFactHandle ) {
        session.bindRuleBase( this, kBase, agenda, initInitFactHandle );
    }

    public void setHandleFactory( FactHandleFactory handleFactory ) {
        session.setHandleFactory( handleFactory );
    }

    public void initEventSupports() {
        session.setAgendaEventSupport( new AgendaEventSupport() );
        session.setRuleRuntimeEventSupport( new RuleRuntimeEventSupport() );
        session.setRuleEventListenerSupport( new RuleEventListenerSupport() );
    }

    @Override
    public void cancelActivation( Activation activation, boolean declarativeAgenda ) {
        session.cancelActivation( activation, declarativeAgenda );
        if (ruleUnitExecutor != null) {
            ruleUnitExecutor.cancelActivation( activation );
        }
    }

    @Override
    public PropagationList getPropagationList() {
        return session.getPropagationList();
    }

    @Override
    public String toString() {
        return session.toString();
    }

    public List iterateObjectsToList() {
        return session.iterateObjectsToList();
    }

    public List iterateNonDefaultEntryPointObjectsToList() {
        return session.iterateNonDefaultEntryPointObjectsToList();
    }

    public Map.Entry[] getActivationParameters( long activationId ) {
        return session.getActivationParameters( activationId );
    }

    public Map getActivationParameters( Activation activation ) {
        return session.getActivationParameters( activation );
    }

    @Override
    public KieRuntimeLogger getLogger() {
        return session.getLogger();
    }

    @Override
    public void setLogger( KieRuntimeLogger logger ) {
        session.setLogger( logger );
    }

    @Override
    public void onSuspend() {
        if (ruleUnitExecutor != null) {
            ruleUnitExecutor.onSuspend();
        }
    }

    @Override
    public void onResume() {
        if (ruleUnitExecutor != null) {
            ruleUnitExecutor.onResume();
        }
    }

    public InternalRuleUnitExecutor getRuleUnitExecutor() {
        return ruleUnitExecutor;
    }

    public StatefulKnowledgeSessionImpl setStateless( boolean stateless ) {
        return session.setStateless( stateless );
    }

    public void initMBeans( String containerId, String kbaseName, String ksessionName ) {
        session.initMBeans( containerId, kbaseName, ksessionName );
    }

    @Override
    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return session.getTruthMaintenanceSystem();
    }

    @Override
    public FactHandleFactory getHandleFactory() {
        return session.getHandleFactory();
    }

    @Override
    public <T> T getKieRuntime( Class<T> cls ) {
        return session.getKieRuntime( cls );
    }

    public <T> T createRuntimeService( Class<T> cls ) {
        return session.createRuntimeService( cls );
    }

    @Override
    public WorkingMemoryEntryPoint getEntryPoint( String name ) {
        return session.getEntryPoint( name );
    }

    @Override
    public Collection<? extends EntryPoint> getEntryPoints() {
        return session.getEntryPoints();
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPointMap() {
        return session.getEntryPointMap();
    }

    @Override
    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        return session.getRuleRuntimeEventListeners();
    }

    @Override
    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return session.getAgendaEventListeners();
    }

    @Override
    public InternalProcessRuntime getProcessRuntime() {
        return session.getProcessRuntime();
    }

    @Override
    public InternalProcessRuntime internalGetProcessRuntime() {
        return session.internalGetProcessRuntime();
    }

    @Override
    public void addEventListener( ProcessEventListener listener ) {
        session.addEventListener( listener );
    }

    @Override
    public Collection<ProcessEventListener> getProcessEventListeners() {
        return session.getProcessEventListeners();
    }

    @Override
    public void removeEventListener( ProcessEventListener listener ) {
        session.removeEventListener( listener );
    }

    @Override
    public KieBase getKieBase() {
        return session.getKieBase();
    }

    @Override
    public void dispose() {
        session.dispose();
    }

    public boolean isAlive() {
        return session.isAlive();
    }

    @Override
    public void destroy() {
        session.destroy();
    }

    public void update( FactHandle factHandle ) {
        session.update( factHandle );
    }

    @Override
    public void abortProcessInstance( long id ) {
        session.abortProcessInstance( id );
    }

    @Override
    public void signalEvent( String type, Object event ) {
        session.signalEvent( type, event );
    }

    @Override
    public void signalEvent( String type, Object event, long processInstanceId ) {
        session.signalEvent( type, event, processInstanceId );
    }

    @Override
    public Globals getGlobals() {
        return session.getGlobals();
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles() {
        return session.getFactHandles();
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles( ObjectFilter filter ) {
        return session.getFactHandles( filter );
    }

    @Override
    public Collection<?> getObjects() {
        return session.getObjects();
    }

    @Override
    public Collection<?> getObjects( ObjectFilter filter ) {
        return session.getObjects( filter );
    }

    @Override
    public <T> T execute( Command<T> command ) {
        return session.execute( command );
    }

    public InternalFactHandle initInitialFact( InternalKnowledgeBase kBase, MarshallerReaderContext context ) {
        return session.initInitialFact( kBase, context );
    }

    public InternalFactHandle initInitialFact( InternalKnowledgeBase kBase, InternalWorkingMemoryEntryPoint entryPoint, EntryPointId epId, MarshallerReaderContext context ) {
        return session.initInitialFact( kBase, entryPoint, epId, context );
    }

    @Override
    public String getEntryPointId() {
        return session.getEntryPointId();
    }

    public QueryResultsImpl getQueryResultsFromRHS( String queryName, Object... arguments ) {
        return session.getQueryResultsFromRHS( queryName, arguments );
    }

    @Override
    public QueryResultsImpl getQueryResults( String queryName, Object... arguments ) {
        return session.getQueryResults( queryName, arguments );
    }

    @Override
    public LiveQuery openLiveQuery( String query, Object[] arguments, ViewChangedEventListener listener ) {
        return session.openLiveQuery( query, arguments, listener );
    }

    @Override
    public void closeLiveQuery( InternalFactHandle factHandle ) {
        session.closeLiveQuery( factHandle );
    }

    @Override
    public EntryPointId getEntryPoint() {
        return session.getEntryPoint();
    }

    @Override
    public InternalWorkingMemory getInternalWorkingMemory() {
        return session.getInternalWorkingMemory();
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        session.writeExternal( out );
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        session.readExternal( in );
    }

    @Override
    public void updateEntryPointsCache() {
        session.updateEntryPointsCache();
    }

    @Override
    public SessionConfiguration getSessionConfiguration() {
        return session.getSessionConfiguration();
    }

    @Override
    public void reset() {
        session.reset();
    }

    public void reset( long handleId, long handleCounter, long propagationCounter ) {
        session.reset( handleId, handleCounter, propagationCounter );
    }

    @Override
    public void setRuleRuntimeEventSupport( RuleRuntimeEventSupport ruleRuntimeEventSupport ) {
        session.setRuleRuntimeEventSupport( ruleRuntimeEventSupport );
    }

    @Override
    public void setAgendaEventSupport( AgendaEventSupport agendaEventSupport ) {
        session.setAgendaEventSupport( agendaEventSupport );
    }

    @Override
    public boolean isSequential() {
        return session.isSequential();
    }

    @Override
    public void addEventListener( RuleRuntimeEventListener listener ) {
        session.addEventListener( listener );
    }

    @Override
    public void removeEventListener( RuleRuntimeEventListener listener ) {
        session.removeEventListener( listener );
    }

    @Override
    public void addEventListener( AgendaEventListener listener ) {
        session.addEventListener( listener );
    }

    @Override
    public void removeEventListener( AgendaEventListener listener ) {
        session.removeEventListener( listener );
    }

    @Override
    public void addEventListener( KieBaseEventListener listener ) {
        session.addEventListener( listener );
    }

    @Override
    public Collection<KieBaseEventListener> getKieBaseEventListeners() {
        return session.getKieBaseEventListeners();
    }

    @Override
    public void removeEventListener( KieBaseEventListener listener ) {
        session.removeEventListener( listener );
    }

    @Override
    public RuleEventListenerSupport getRuleEventSupport() {
        return session.getRuleEventSupport();
    }

    @Override
    public void addEventListener( RuleEventListener listener ) {
        session.addEventListener( listener );
    }

    @Override
    public void removeEventListener( RuleEventListener listener ) {
        session.removeEventListener( listener );
    }

    @Override
    public FactHandleFactory getFactHandleFactory() {
        return session.getFactHandleFactory();
    }

    @Override
    public void setGlobal( String identifier, Object value ) {
        session.setGlobal( identifier, value );
    }

    @Override
    public void removeGlobal( String identifier ) {
        session.removeGlobal( identifier );
    }

    @Override
    public void setGlobalResolver( GlobalResolver globalResolver ) {
        session.setGlobalResolver( globalResolver );
    }

    @Override
    public GlobalResolver getGlobalResolver() {
        return session.getGlobalResolver();
    }

    @Override
    public Calendars getCalendars() {
        return session.getCalendars();
    }

    @Override
    public int getId() {
        return session.getId();
    }

    @Override
    public long getIdentifier() {
        return session.getIdentifier();
    }

    @Override
    public void setIdentifier( long id ) {
        session.setIdentifier( id );
    }

    @Override
    public Object getGlobal( String identifier ) {
        return session.getGlobal( identifier );
    }

    @Override
    public Environment getEnvironment() {
        return session.getEnvironment();
    }

    @Override
    public InternalAgenda getAgenda() {
        return session.getAgenda();
    }

    @Override
    public void clearAgenda() {
        session.clearAgenda();
    }

    @Override
    public void clearAgendaGroup( String group ) {
        session.clearAgendaGroup( group );
    }

    @Override
    public void clearActivationGroup( String group ) {
        session.clearActivationGroup( group );
    }

    @Override
    public void clearRuleFlowGroup( String group ) {
        session.clearRuleFlowGroup( group );
    }

    @Override
    public InternalKnowledgeBase getKnowledgeBase() {
        return session.getKnowledgeBase();
    }

    @Override
    public void halt() {
        session.halt();
    }

    @Override
    public int fireAllRules() {
        return session.fireAllRules();
    }

    @Override
    public int fireAllRules( int fireLimit ) {
        return session.fireAllRules( fireLimit );
    }

    @Override
    public int fireAllRules( AgendaFilter agendaFilter ) {
        return session.fireAllRules( agendaFilter );
    }

    @Override
    public int fireAllRules( AgendaFilter agendaFilter, int fireLimit ) {
        return session.fireAllRules( agendaFilter, fireLimit );
    }

    @Override
    public void fireUntilHalt() {
        session.fireUntilHalt();
    }

    @Override
    public void fireUntilHalt( AgendaFilter agendaFilter ) {
        session.fireUntilHalt( agendaFilter );
    }

    @Override
    public Object getObject( FactHandle handle ) {
        return session.getObject( handle );
    }

    @Override
    public ObjectStore getObjectStore() {
        return session.getObjectStore();
    }

    @Override
    public FactHandle getFactHandle( Object object ) {
        return session.getFactHandle( object );
    }

    @Override
    public FactHandle getFactHandleByIdentity( Object object ) {
        return session.getFactHandleByIdentity( object );
    }

    @Override
    public Iterator iterateObjects() {
        return session.iterateObjects();
    }

    @Override
    public Iterator iterateObjects( ObjectFilter filter ) {
        return session.iterateObjects( filter );
    }

    @Override
    public Iterator<InternalFactHandle> iterateFactHandles() {
        return session.iterateFactHandles();
    }

    @Override
    public Iterator<InternalFactHandle> iterateFactHandles( ObjectFilter filter ) {
        return session.iterateFactHandles( filter );
    }

    @Override
    public void setFocus( String focus ) {
        session.setFocus( focus );
    }

    @Override
    public FactHandle insertAsync( Object object ) {
        return session.insertAsync( object );
    }

    @Override
    public FactHandle insert( Object object ) {
        return session.insert( object );
    }

    @Override
    public FactHandle insert( Object object, boolean dynamic ) {
        return session.insert( object, dynamic );
    }

    @Override
    public void submit( AtomicAction action ) {
        session.submit( action );
    }

    @Override
    public void updateTraits( InternalFactHandle h, BitMask mask, Class<?> modifiedClass, Activation activation ) {
        session.updateTraits( h, mask, modifiedClass, activation );
    }

    @Override
    public <T, K, X extends TraitableBean> Thing<K> shed( Activation activation, TraitableBean<K, X> core, Class<T> trait ) {
        return session.shed( activation, core, trait );
    }

    @Override
    public <T, K> T don( Activation activation, K core, Collection<Class<? extends Thing>> traits, boolean b, Mode[] modes ) {
        return session.don( activation, core, traits, b, modes );
    }

    @Override
    public <T, K> T don( Activation activation, K core, Class<T> trait, boolean b, Mode[] modes ) {
        return session.don( activation, core, trait, b, modes );
    }

    @Override
    public FactHandle insert( Object object, boolean dynamic, RuleImpl rule, TerminalNode terminalNode ) {
        return session.insert( object, dynamic, rule, terminalNode );
    }

    @Override
    public void retract( FactHandle handle ) {
        session.retract( handle );
    }

    @Override
    public void delete( FactHandle handle ) {
        session.delete( handle );
    }

    @Override
    public void delete( FactHandle handle, FactHandle.State fhState ) {
        session.delete( handle, fhState );
    }

    @Override
    public void delete( FactHandle factHandle, RuleImpl rule, TerminalNode terminalNode ) {
        session.delete( factHandle, rule, terminalNode );
    }

    @Override
    public void delete( FactHandle factHandle, RuleImpl rule, TerminalNode terminalNode, FactHandle.State fhState ) {
        session.delete( factHandle, rule, terminalNode, fhState );
    }

    @Override
    public EntryPointNode getEntryPointNode() {
        return session.getEntryPointNode();
    }

    @Override
    public void update( FactHandle handle, Object object ) {
        session.update( handle, object );
    }

    @Override
    public void update( FactHandle handle, Object object, String... modifiedProperties ) {
        session.update( handle, object, modifiedProperties );
    }

    @Override
    public void update( FactHandle factHandle, Object object, BitMask mask, Class<?> modifiedClass, Activation activation ) {
        session.update( factHandle, object, mask, modifiedClass, activation );
    }

    @Override
    public void executeQueuedActions() {
        session.executeQueuedActions();
    }

    @Override
    public void queueWorkingMemoryAction( WorkingMemoryAction action ) {
        session.queueWorkingMemoryAction( action );
    }

    @Override
    public <T extends Memory> T getNodeMemory( MemoryFactory<T> node ) {
        return session.getNodeMemory( node );
    }

    @Override
    public void clearNodeMemory( MemoryFactory node ) {
        session.clearNodeMemory( node );
    }

    @Override
    public NodeMemories getNodeMemories() {
        return session.getNodeMemories();
    }

    @Override
    public RuleRuntimeEventSupport getRuleRuntimeEventSupport() {
        return session.getRuleRuntimeEventSupport();
    }

    @Override
    public AgendaEventSupport getAgendaEventSupport() {
        return session.getAgendaEventSupport();
    }

    @Override
    public void setAsyncExceptionHandler( AsyncExceptionHandler handler ) {
        session.setAsyncExceptionHandler( handler );
    }

    @Override
    public long getNextPropagationIdCounter() {
        return session.getNextPropagationIdCounter();
    }

    public long getPropagationIdCounter() {
        return session.getPropagationIdCounter();
    }

    @Override
    public Lock getLock() {
        return session.getLock();
    }

    @Override
    public ProcessInstance startProcess( String processId ) {
        return session.startProcess( processId );
    }

    @Override
    public ProcessInstance startProcess( String processId, Map<String, Object> parameters ) {
        return session.startProcess( processId, parameters );
    }

    @Override
    public ProcessInstance startProcess( String processId, AgendaFilter agendaFilter ) {
        return session.startProcess( processId, agendaFilter );
    }

    @Override
    public ProcessInstance startProcess( String processId, Map<String, Object> parameters, AgendaFilter agendaFilter ) {
        return session.startProcess( processId, parameters, agendaFilter );
    }

    @Override
    public ProcessInstance createProcessInstance( String processId, Map<String, Object> parameters ) {
        return session.createProcessInstance( processId, parameters );
    }

    @Override
    public ProcessInstance startProcessInstance( long processInstanceId ) {
        return session.startProcessInstance( processInstanceId );
    }

    @Override
    public Collection<ProcessInstance> getProcessInstances() {
        return session.getProcessInstances();
    }

    @Override
    public ProcessInstance getProcessInstance( long processInstanceId ) {
        return session.getProcessInstance( processInstanceId );
    }

    @Override
    public ProcessInstance startProcess( String processId, CorrelationKey correlationKey, Map<String, Object> parameters ) {
        return session.startProcess( processId, correlationKey, parameters );
    }

    @Override
    public ProcessInstance createProcessInstance( String processId, CorrelationKey correlationKey, Map<String, Object> parameters ) {
        return session.createProcessInstance( processId, correlationKey, parameters );
    }

    @Override
    public ProcessInstance getProcessInstance( CorrelationKey correlationKey ) {
        return session.getProcessInstance( correlationKey );
    }

    @Override
    public ProcessInstance getProcessInstance( long processInstanceId, boolean readOnly ) {
        return session.getProcessInstance( processInstanceId, readOnly );
    }

    @Override
    public WorkItemManager getWorkItemManager() {
        return session.getWorkItemManager();
    }

    @Override
    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint( String name ) {
        return session.getWorkingMemoryEntryPoint( name );
    }

    public Map<String, WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
        return session.getWorkingMemoryEntryPoints();
    }

    @Override
    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        return session.getObjectTypeConfigurationRegistry();
    }

    @Override
    public InternalFactHandle getInitialFactHandle() {
        return session.getInitialFactHandle();
    }

    public void setInitialFactHandle( InternalFactHandle initialFactHandle ) {
        session.setInitialFactHandle( initialFactHandle );
    }

    @Override
    public TimerService getTimerService() {
        return session.getTimerService();
    }

    @Override
    public SessionClock getSessionClock() {
        return session.getSessionClock();
    }

    @Override
    public void startBatchExecution() {
        session.startBatchExecution();
    }

    @Override
    public void endBatchExecution() {
        session.endBatchExecution();
    }

    @Override
    public InternalKnowledgeRuntime getKnowledgeRuntime() {
        return session.getKnowledgeRuntime();
    }

    @Override
    public void registerChannel( String name, Channel channel ) {
        session.registerChannel( name, channel );
    }

    @Override
    public void unregisterChannel( String name ) {
        session.unregisterChannel( name );
    }

    @Override
    public Map<String, Channel> getChannels() {
        return session.getChannels();
    }

    @Override
    public long getFactCount() {
        return session.getFactCount();
    }

    @Override
    public long getTotalFactCount() {
        return session.getTotalFactCount();
    }

    @Override
    public void startOperation() {
        session.startOperation();
    }

    @Override
    public void setEndOperationListener( EndOperationListener listener ) {
        session.setEndOperationListener( listener );
    }

    @Override
    public void endOperation() {
        session.endOperation();
    }

    @Override
    public long getIdleTime() {
        return session.getIdleTime();
    }

    @Override
    public long getLastIdleTimestamp() {
        return session.getLastIdleTimestamp();
    }

    @Override
    public void prepareToFireActivation() {
        session.prepareToFireActivation();
    }

    @Override
    public void activationFired() {
        session.activationFired();
    }

    @Override
    public long getTimeToNextJob() {
        return session.getTimeToNextJob();
    }

    @Override
    public void addPropagation( PropagationEntry propagationEntry ) {
        session.addPropagation( propagationEntry );
    }

    @Override
    public void flushPropagations() {
        session.flushPropagations();
    }

    @Override
    public void notifyWaitOnRest() {
        session.notifyWaitOnRest();
    }

    @Override
    public Iterator<? extends PropagationEntry> getActionsIterator() {
        return session.getActionsIterator();
    }

    @Override
    public void activate() {
        session.activate();
    }

    @Override
    public void deactivate() {
        session.deactivate();
    }

    @Override
    public boolean tryDeactivate() {
        return session.tryDeactivate();
    }

    @Override
    public ProcessInstance startProcessFromNodeIds(String processId, Map<String, Object> params, String... nodeIds) {
        return session.startProcessFromNodeIds(processId, params, nodeIds);
    }

    @Override
    public ProcessInstance startProcessFromNodeIds(String processId, CorrelationKey key, Map<String, Object> params, String... nodeIds) {
        return session.startProcessFromNodeIds(processId, key, params, nodeIds);
    }
}
