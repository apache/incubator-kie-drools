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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
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

public interface InternalWorkingMemory
    extends
        WorkingMemory {
    public int getId();
    
    public void setId(int id);

    public void setRuleRuntimeEventSupport(RuleRuntimeEventSupport workingMemoryEventSupport);

    ///public ObjectHashMap getAssertMap();

    public void setAgendaEventSupport(AgendaEventSupport agendaEventSupport);

    public Memory getNodeMemory(MemoryFactory node);

    public void clearNodeMemory(MemoryFactory node);
    
    public NodeMemories getNodeMemories();

    public long getNextPropagationIdCounter();

    //public ObjectHashMap getFactHandleMap()
    
    public ObjectStore getObjectStore();

    public void executeQueuedActions();

    public void queueWorkingMemoryAction(final WorkingMemoryAction action);

    public FactHandleFactory getFactHandleFactory();
    
    public EntryPointId getEntryPoint();
    
    public EntryPointNode getEntryPointNode();

    EntryPoint getEntryPoint(String name);

    public void insert(final InternalFactHandle handle,
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
    public FactHandle getFactHandleByIdentity(final Object object);

    void delete(final FactHandle factHandle,
                       final RuleImpl rule,
                       final Activation activation);

    public Lock getLock();

    public boolean isSequential();
    
    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry();
    
    public InternalFactHandle getInitialFactHandle();
    
    public Calendars getCalendars();
    
    /**
     * Returns the TimerService instance (session clock) for this
     * session.
     * 
     * @return
     */
    public TimerService getTimerService();

    public void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime);
    
    public InternalKnowledgeRuntime getKnowledgeRuntime();
    
    /**
     * Returns a map of channel Id->Channel of all channels in
     * this working memory
     * 
     * @return
     */
    public Map< String, Channel> getChannels();
    
    public Collection< ? extends EntryPoint> getEntryPoints();

    public SessionConfiguration getSessionConfiguration();
    
    
    public void startBatchExecution(ExecutionResultImpl results);
    
    public ExecutionResultImpl getExecutionResult();
    
    public void endBatchExecution();
    
    /**
     * This method must be called before starting any new work in the engine,
     * like inserting a new fact or firing a new rule. It will reset the engine
     * idle time counter.
     * 
     * This method must be extremely light to avoid contentions when called by 
     * multiple threads/entry-points
     */
    public void startOperation();

    /**
     * This method must be called after finishing any work in the engine,
     * like inserting a new fact or firing a new rule. It will reset the engine
     * idle time counter.
     * 
     * This method must be extremely light to avoid contentions when called by 
     * multiple threads/entry-points
     */
    public void endOperation();
    
    /**
     * Returns the number of time units (usually ms) that the engine is idle
     * according to the session clock or -1 if it is not idle.
     * 
     * This method is not synchronised and might return an approximate value.
     *  
     * @return
     */
    public long getIdleTime();
    
    /**
     * Returns the number of time units (usually ms) to
     * the next scheduled job
     * 
     * @return the number of time units until the next scheduled job or -1 if
     *         there is no job scheduled
     */
    public long getTimeToNextJob();
    
    public void updateEntryPointsCache();
    
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
    public void activationFired();
    
    /**
     * Returns the total number of facts in the working memory, i.e., counting
     * all facts from all entry points. This is an approximate value and may not
     * be accurate due to the concurrent nature of the entry points.
     * 
     * @return
     */
    public long getTotalFactCount();
    
    public DateFormats getDateFormats();
    
    InternalProcessRuntime getProcessRuntime();

    void closeLiveQuery(InternalFactHandle factHandle);
}
