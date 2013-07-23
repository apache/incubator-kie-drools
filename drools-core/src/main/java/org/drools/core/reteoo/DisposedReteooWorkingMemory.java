/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.reteoo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

import org.drools.core.Agenda;
import org.drools.core.FactException;
import org.drools.core.FactHandle;
import org.drools.core.QueryResults;
import org.drools.core.RuleBase;
import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.EndOperationListener;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.TimedRuleExecution;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.event.AgendaEventListener;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleBaseEventListener;
import org.drools.core.event.WorkingMemoryEventListener;
import org.drools.core.event.WorkingMemoryEventSupport;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.Rule;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaFilter;
import org.drools.core.spi.AsyncExceptionHandler;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.time.TimerService;
import org.drools.core.type.DateFormats;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.time.SessionClock;

@SuppressWarnings( "deprecation" )
public class DisposedReteooWorkingMemory implements ReteooWorkingMemoryInterface {
    private static final String ERRORMSG = "Illegal method call. This session was previously disposed."; 

    public static final DisposedReteooWorkingMemory INSTANCE = new DisposedReteooWorkingMemory();
    
    private DisposedReteooWorkingMemory() {}
    
    public void update( FactHandle handle, Object object, long mask, Class<?> modifiedClass, Activation activation ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public void delete( FactHandle handle, Rule rule, Activation activation ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public FactHandle insert( Object object, Object value, boolean dynamic, boolean logical, Rule rule, Activation activation ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public FactHandle insertLogical( Object object, boolean dynamic ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public int getId() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void setId( int id ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void setWorkingMemoryEventSupport( WorkingMemoryEventSupport workingMemoryEventSupport ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void setAgendaEventSupport( AgendaEventSupport agendaEventSupport ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public Memory getNodeMemory( MemoryFactory node ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void clearNodeMemory( MemoryFactory node ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public NodeMemories getNodeMemories() {
        throw new IllegalStateException( ERRORMSG );
    }

    public long getNextPropagationIdCounter() {
        throw new IllegalStateException( ERRORMSG );
    }

    public ObjectStore getObjectStore() {
        throw new IllegalStateException( ERRORMSG );
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void executeQueuedActions() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void queueWorkingMemoryAction( WorkingMemoryAction action ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public FactHandleFactory getFactHandleFactory() {
        throw new IllegalStateException( ERRORMSG );
    }

    public EntryPointId getEntryPoint() {
        throw new IllegalStateException( ERRORMSG );
    }

    public EntryPointNode getEntryPointNode() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void insert( InternalFactHandle handle, Object object, Rule rule, Activation activation, ObjectTypeConf typeConf ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public FactHandle getFactHandleByIdentity( Object object ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public Lock getLock() {
        throw new IllegalStateException( ERRORMSG );
    }

    public boolean isSequential() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void addLIANodePropagation( LIANodePropagation liaNodePropagation ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        throw new IllegalStateException( ERRORMSG );
    }

    public InternalFactHandle getInitialFactHandle() {
        throw new IllegalStateException( ERRORMSG );
    }

    public Calendars getCalendars() {
        throw new IllegalStateException( ERRORMSG );
    }

    public TimerService getTimerService() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void setKnowledgeRuntime( InternalKnowledgeRuntime kruntime ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public InternalKnowledgeRuntime getKnowledgeRuntime() {
        throw new IllegalStateException( ERRORMSG );
    }

    public Map<String, Channel> getChannels() {
        throw new IllegalStateException( ERRORMSG );
    }

    public Map<String, ? extends EntryPoint> getEntryPoints() {
        throw new IllegalStateException( ERRORMSG );
    }

    public SessionConfiguration getSessionConfiguration() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void startBatchExecution( ExecutionResultImpl results ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public ExecutionResultImpl getExecutionResult() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void endBatchExecution() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void startOperation() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void endOperation() {
        throw new IllegalStateException( ERRORMSG );
    }

    public long getIdleTime() {
        throw new IllegalStateException( ERRORMSG );
    }

    public long getTimeToNextJob() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void updateEntryPointsCache() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void prepareToFireActivation() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void activationFired() {
        throw new IllegalStateException( ERRORMSG );
    }

    public long getTotalFactCount() {
        throw new IllegalStateException( ERRORMSG );
    }

    public DateFormats getDateFormats() {
        throw new IllegalStateException( ERRORMSG );
    }

    public InternalProcessRuntime getProcessRuntime() {
        throw new IllegalStateException( ERRORMSG );
    }

    @Override
    public void closeLiveQuery(InternalFactHandle factHandle) {
        throw new IllegalStateException( ERRORMSG );
    }

    public Agenda getAgenda() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void setGlobal( String identifier, Object value ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public Object getGlobal( String identifier ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public Environment getEnvironment() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void setGlobalResolver( GlobalResolver globalResolver ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public GlobalResolver getGlobalResolver() {
        throw new IllegalStateException( ERRORMSG );
    }

    public RuleBase getRuleBase() {
        throw new IllegalStateException( ERRORMSG );
    }

    public int fireAllRules() throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public int fireAllRules( AgendaFilter agendaFilter ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public int fireAllRules( int fireLimit ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public int fireAllRules( AgendaFilter agendaFilter, int fireLimit ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public Object getObject( org.kie.api.runtime.rule.FactHandle handle ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public FactHandle getFactHandle( Object object ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public Iterator<?> iterateObjects() {
        throw new IllegalStateException( ERRORMSG );
    }

    public Iterator<?> iterateObjects( ObjectFilter filter ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public Iterator<?> iterateFactHandles() {
        throw new IllegalStateException( ERRORMSG );
    }

    public Iterator<?> iterateFactHandles( ObjectFilter filter ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void setFocus( String focus ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public QueryResults getQueryResults( String query, Object... arguments ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void setAsyncExceptionHandler( AsyncExceptionHandler handler ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void clearAgenda() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void clearAgendaGroup( String group ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void clearActivationGroup( String group ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void clearRuleFlowGroup( String group ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public ProcessInstance startProcess( String processId ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public ProcessInstance startProcess( String processId, Map<String, Object> parameters ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public Collection<ProcessInstance> getProcessInstances() {
        throw new IllegalStateException( ERRORMSG );
    }

    public ProcessInstance getProcessInstance( long id ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public ProcessInstance getProcessInstance( long id, boolean readOnly ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public WorkItemManager getWorkItemManager() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void halt() {
        throw new IllegalStateException( ERRORMSG );
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint( String id ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public SessionClock getSessionClock() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void addEventListener( WorkingMemoryEventListener listener ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void removeEventListener( WorkingMemoryEventListener listener ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public List getWorkingMemoryEventListeners() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void addEventListener( AgendaEventListener listener ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void removeEventListener( AgendaEventListener listener ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public List getAgendaEventListeners() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void addEventListener( RuleBaseEventListener listener ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void removeEventListener( RuleBaseEventListener listener ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public List<RuleBaseEventListener> getRuleBaseEventListeners() {
        throw new IllegalStateException( ERRORMSG );
    }

    public FactHandle insert( Object object ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public FactHandle insert( Object object, boolean dynamic ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public void retract( org.kie.api.runtime.rule.FactHandle handle ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public void delete( org.kie.api.runtime.rule.FactHandle handle ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public void update( org.kie.api.runtime.rule.FactHandle handle, Object object ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public void dispose() {
        // lets not raise an exception on a second dispose call
    }

    public String getEntryPointId() {
        throw new IllegalStateException( ERRORMSG );
    }

    public Collection<? extends Object> getObjects() {
        throw new IllegalStateException( ERRORMSG );
    }

    public Collection<? extends Object> getObjects( ObjectFilter filter ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public <T extends org.kie.api.runtime.rule.FactHandle> Collection<T> getFactHandles() {
        throw new IllegalStateException( ERRORMSG );
    }

    public <T extends org.kie.api.runtime.rule.FactHandle> Collection<T> getFactHandles( ObjectFilter filter ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public long getFactCount() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void update( org.kie.api.runtime.rule.FactHandle handle, Object object, long mask, Class<?> modifiedClass, Activation activation ) throws FactException {
        throw new IllegalStateException( ERRORMSG );
    }

    public InternalWorkingMemory getInternalWorkingMemory() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void reset() {
        throw new IllegalStateException( ERRORMSG );
    }

    public AgendaEventSupport getAgendaEventSupport() {
        throw new IllegalStateException( ERRORMSG );
    }

    public WorkingMemoryEventSupport getWorkingMemoryEventSupport() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void addEventListener( ProcessEventListener listener ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void removeEventListener( ProcessEventListener listener ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        throw new IllegalStateException( ERRORMSG );
    }

    public Collection<? extends EntryPoint> getWorkingMemoryEntryPoints() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void fireUntilHalt() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void fireUntilHalt( AgendaFilter agendaFilterWrapper ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public ProcessInstance createProcessInstance( String processId, Map<String, Object> parameters ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public ProcessInstance startProcessInstance( long processInstanceId ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void unregisterExitPoint( String name ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void registerChannel( String name, Channel channel ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void unregisterChannel( String name ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public Queue<WorkingMemoryAction> getActionQueue() {
        throw new IllegalStateException( ERRORMSG );
    }

    public LiveQuery openLiveQuery( String query, Object[] arguments, ViewChangedEventListener listener ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public void setEndOperationListener( EndOperationListener listener ) {
        throw new IllegalStateException( ERRORMSG );
    }

    public long getLastIdleTimestamp() {
        throw new IllegalStateException( ERRORMSG );
    }

    public Queue<TimedRuleExecution> getTimedExecutionsQueue() {
        throw new IllegalStateException( ERRORMSG );
    }

    public void setTimedExecutionsQueue(Queue<TimedRuleExecution> timedExecutionsQueue) {
        throw new IllegalStateException( ERRORMSG );
    }
}
