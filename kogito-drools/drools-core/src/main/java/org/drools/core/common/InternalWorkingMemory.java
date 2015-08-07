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

package org.drools.core.common;

import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.EntryPointId;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.spi.Activation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.time.TimerService;
import org.drools.core.type.DateFormats;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public interface InternalWorkingMemory
    extends WorkingMemory {

    InternalAgenda getAgenda();

    int getId();
    void setId(Long id);

    void setRuleRuntimeEventSupport(RuleRuntimeEventSupport workingMemoryEventSupport);

    void setAgendaEventSupport(AgendaEventSupport agendaEventSupport);

    Memory getNodeMemory(MemoryFactory node);

    void clearNodeMemory(MemoryFactory node);
    
    NodeMemories getNodeMemories();

    long getNextPropagationIdCounter();

    ObjectStore getObjectStore();

    void executeQueuedActionsForRete();

    void queueWorkingMemoryAction(final WorkingMemoryAction action);

    FactHandleFactory getFactHandleFactory();
    
    EntryPointId getEntryPoint();
    
    EntryPointNode getEntryPointNode();

    EntryPoint getEntryPoint(String name);

    void insert(final InternalFactHandle handle,
                       final Object object,
                       final RuleImpl rule,
                       final Activation activation,
                       ObjectTypeConf typeConf);
    
    /**
     * Looks for the fact handle associated to the given object
     * by looking up the object IDENTITY (==), even if rule base
     * is configured to AssertBehavior.EQUALITY.
     * 
     * @param object
     * @return null if fact handle not found
     */
    FactHandle getFactHandleByIdentity(final Object object);

    void delete(final FactHandle factHandle,
                       final RuleImpl rule,
                       final Activation activation);

    Lock getLock();

    boolean isSequential();
    
    ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();
    
    InternalFactHandle getInitialFactHandle();
    
    Calendars getCalendars();
    
    /**
     * Returns the TimerService instance (session clock) for this
     * session.
     * 
     * @return
     */
    TimerService getTimerService();

    InternalKnowledgeRuntime getKnowledgeRuntime();
    
    /**
     * Returns a map of channel Id->Channel of all channels in
     * this working memory
     * 
     * @return
     */
    Map< String, Channel> getChannels();
    
    Collection< ? extends EntryPoint> getEntryPoints();

    SessionConfiguration getSessionConfiguration();
    
    void startBatchExecution(ExecutionResultImpl results);
    
    ExecutionResultImpl getExecutionResult();
    
    void endBatchExecution();
    
    /**
     * This method must be called before starting any new work in the engine,
     * like inserting a new fact or firing a new rule. It will reset the engine
     * idle time counter.
     * 
     * This method must be extremely light to avoid contentions when called by 
     * multiple threads/entry-points
     */
    void startOperation();

    /**
     * This method must be called after finishing any work in the engine,
     * like inserting a new fact or firing a new rule. It will reset the engine
     * idle time counter.
     * 
     * This method must be extremely light to avoid contentions when called by 
     * multiple threads/entry-points
     */
    void endOperation();
    
    /**
     * Returns the number of time units (usually ms) that the engine is idle
     * according to the session clock or -1 if it is not idle.
     * 
     * This method is not synchronised and might return an approximate value.
     *  
     * @return
     */
    long getIdleTime();
    
    /**
     * Returns the number of time units (usually ms) to
     * the next scheduled job
     * 
     * @return the number of time units until the next scheduled job or -1 if
     *         there is no job scheduled
     */
    long getTimeToNextJob();
    
    void updateEntryPointsCache();
    
    /**
     * This method is called by the agenda before firing a new activation
     * to ensure the working memory is in a safe state to fire the activation.
     */
    public void prepareToFireActivation();
    
    /**
     * This method is called by the agenda right after an activation was fired
     * to allow the working memory to resume any activities blocked during 
     * activation firing. 
     */
    void activationFired();
    
    /**
     * Returns the total number of facts in the working memory, i.e., counting
     * all facts from all entry points. This is an approximate value and may not
     * be accurate due to the concurrent nature of the entry points.
     * 
     * @return
     */
    long getTotalFactCount();
    
    DateFormats getDateFormats();
    
    InternalProcessRuntime getProcessRuntime();
    InternalProcessRuntime internalGetProcessRuntime();

    void closeLiveQuery(InternalFactHandle factHandle);

    void addPropagation(PropagationEntry propagationEntry);

    void flushPropagations();
    void flushPropagationsOnFireUntilHalt( boolean fired );
    void flushPropagationsOnFireUntilHalt( boolean fired, PropagationEntry propagationEntry );
    void flushNonMarshallablePropagations();

    void notifyEngineInactive();

    boolean hasPendingPropagations();
    PropagationEntry takeAllPropagations();

    Iterator<? extends PropagationEntry> getActionsIterator();

    void removeGlobal(String identifier);

    void notifyHalt();
}
