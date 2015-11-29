/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.reteoo.common;

import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.LIANodePropagation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.PropagationContext;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.rule.AgendaFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReteWorkingMemory extends StatefulKnowledgeSessionImpl {

    private List<LIANodePropagation> liaPropagations;

    private Queue<WorkingMemoryAction> actionQueue;

    private AtomicBoolean evaluatingActionQueue = new AtomicBoolean(false);

    /** Flag to determine if a rule is currently being fired. */
    private volatile AtomicBoolean firing = new AtomicBoolean(false);

    public ReteWorkingMemory() {
    }

    public ReteWorkingMemory(long id, InternalKnowledgeBase kBase) {
        super(id, kBase);
    }

    public ReteWorkingMemory(long id, InternalKnowledgeBase kBase, boolean initInitFactHandle, SessionConfiguration config, Environment environment) {
        super(id, kBase, initInitFactHandle, config, environment);
    }

    public ReteWorkingMemory(long id, InternalKnowledgeBase kBase, FactHandleFactory handleFactory, long propagationContext, SessionConfiguration config, InternalAgenda agenda, Environment environment) {
        super(id, kBase, handleFactory, propagationContext, config, agenda, environment);
    }

    public ReteWorkingMemory(long id, InternalKnowledgeBase kBase, FactHandleFactory handleFactory, InternalFactHandle initialFactHandle, long propagationContext, SessionConfiguration config, Environment environment, RuleRuntimeEventSupport workingMemoryEventSupport, AgendaEventSupport agendaEventSupport, RuleEventListenerSupport ruleEventListenerSupport, InternalAgenda agenda) {
        super(id, kBase, handleFactory, false, propagationContext, config, environment, workingMemoryEventSupport, agendaEventSupport, ruleEventListenerSupport, agenda);
    }

    @Override
    protected void init() {
        this.actionQueue = new ConcurrentLinkedQueue<WorkingMemoryAction>();
        this.propagationList = new RetePropagationList(this);
    }

    @Override
    public void reset() {
        super.reset();
        actionQueue.clear();
    }

    @Override
    public void reset(int handleId,
                      long handleCounter,
                      long propagationCounter) {
        super.reset(handleId, handleCounter, propagationCounter );
        if (liaPropagations != null) liaPropagations.clear();
        actionQueue.clear();
    }

    @Override
    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        WorkingMemoryEntryPoint ep = this.entryPoints.get(name);
        return ep != null ? new ReteWorkingMemoryEntryPoint( this, ep ) : null;
    }

    public void addLIANodePropagation(LIANodePropagation liaNodePropagation) {
        if (liaPropagations == null) liaPropagations = new ArrayList<LIANodePropagation>();
        liaPropagations.add( liaNodePropagation );
    }

    private final Integer syncLock = 42;
    public void initInitialFact() {
        if ( initialFactHandle == null ) {
            synchronized ( syncLock ) {
                if ( initialFactHandle == null ) {
                    // double check, inside of sync point incase some other thread beat us to it.
                    initInitialFact(kBase, null);
                }
            }
        }
    }

    @Override
    public void fireUntilHalt(final AgendaFilter agendaFilter) {
        initInitialFact();
        super.fireUntilHalt( agendaFilter );
    }

    @Override
    public int fireAllRules(final AgendaFilter agendaFilter,
                            int fireLimit) {
        checkAlive();
        if ( this.firing.compareAndSet( false,
                                        true ) ) {
            initInitialFact();

            try {
                startOperation();

                return internalFireAllRules(agendaFilter, fireLimit);
            } finally {
                endOperation();
                this.firing.set( false );
            }
        }
        return 0;
    }

    private int internalFireAllRules(AgendaFilter agendaFilter, int fireLimit) {
        int fireCount = 0;
        try {
            kBase.readLock();

            // If we're already firing a rule, then it'll pick up the firing for any other assertObject(..) that get
            // nested inside, avoiding concurrent-modification exceptions, depending on code paths of the actions.
            if ( liaPropagations != null && isSequential() ) {
                for ( LIANodePropagation liaPropagation : liaPropagations ) {
                    ( liaPropagation ).doPropagation( this );
                }
            }

            // do we need to call this in advance?
            executeQueuedActionsForRete();

            fireCount = this.agenda.fireAllRules( agendaFilter,
                                                  fireLimit );
        } finally {
            kBase.readUnlock();
            if (kBase.flushModifications()) {
                fireCount += internalFireAllRules(agendaFilter, fireLimit);
            }
        }
        return fireCount;
    }

    @Override
    public void closeLiveQuery(final InternalFactHandle factHandle) {

        try {
            startOperation();
            this.kBase.readLock();
            this.lock.lock();

            final PropagationContext pCtx = pctxFactory.createPropagationContext(getNextPropagationIdCounter(), PropagationContext.INSERTION,
                                                                                 null, null, factHandle, getEntryPoint());

            getEntryPointNode().retractQuery( factHandle,
                                              pCtx,
                                              this );

            pCtx.evaluateActionQueue(this);

            getFactHandleFactory().destroyFactHandle( factHandle );

        } finally {
            this.lock.unlock();
            this.kBase.readUnlock();
            endOperation();
        }
    }

    @Override
    protected BaseNode[] evalQuery(String queryName, DroolsQuery queryObject, InternalFactHandle handle, PropagationContext pCtx) {
        initInitialFact();

        BaseNode[] tnodes = kBase.getReteooBuilder().getTerminalNodesForQuery( queryName );
        // no need to call retract, as no leftmemory used.
        getEntryPointNode().assertQuery( handle,
                                         pCtx,
                                         this );

        pCtx.evaluateActionQueue( this );
        return tnodes;
    }

    public Collection<WorkingMemoryAction> getActionQueue() {
        return actionQueue;
    }

    @Override
    public void queueWorkingMemoryAction(final WorkingMemoryAction action) {
        try {
            startOperation();
            actionQueue.add(action);
            notifyWaitOnRest();
        } finally {
            endOperation();
        }
    }

    public void addPropagation(PropagationEntry propagationEntry) {
        if (propagationEntry instanceof WorkingMemoryAction) {
            actionQueue.add((WorkingMemoryAction) propagationEntry);
        } else {
            super.addPropagation(propagationEntry);
        }
    }

    @Override
    public void executeQueuedActionsForRete() {
        try {
            startOperation();
            if ( evaluatingActionQueue.compareAndSet( false,
                                                      true ) ) {
                try {
                    if ( actionQueue!= null && !actionQueue.isEmpty() ) {
                        WorkingMemoryAction action;

                        while ( (action = actionQueue.poll()) != null ) {
                            try {
                                action.execute( (InternalWorkingMemory) this );
                            } catch ( Exception e ) {
                                throw new RuntimeException( "Unexpected exception executing action " + action.toString(),
                                                            e );
                            }
                        }
                    }
                } finally {
                    evaluatingActionQueue.compareAndSet( true,
                                                         false );
                }
            }
        } finally {
            endOperation();
        }
    }

    @Override
    public Iterator<? extends PropagationEntry> getActionsIterator() {
        return actionQueue.iterator();
    }
}
