package org.drools.reteoo.common;

import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.LIANodePropagation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.PropagationContext;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.rule.AgendaFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReteWorkingMemory extends StatefulKnowledgeSessionImpl {

    private List<LIANodePropagation> liaPropagations;

    public ReteWorkingMemory() {
    }

    public ReteWorkingMemory(long id, InternalKnowledgeBase kBase) {
        super(id, kBase);
    }

    public ReteWorkingMemory(long id, InternalKnowledgeBase kBase, boolean initInitFactHandle, SessionConfiguration config, Environment environment) {
        super(id, kBase, initInitFactHandle, config, environment);
    }

    public ReteWorkingMemory(long id, InternalKnowledgeBase kBase, FactHandleFactory handleFactory, InternalFactHandle initialFactHandle, long propagationContext, SessionConfiguration config, InternalAgenda agenda, Environment environment) {
        super(id, kBase, handleFactory, initialFactHandle, propagationContext, config, agenda, environment);
    }

    public ReteWorkingMemory(long id, InternalKnowledgeBase kBase, FactHandleFactory handleFactory, InternalFactHandle initialFactHandle, long propagationContext, SessionConfiguration config, Environment environment, RuleRuntimeEventSupport workingMemoryEventSupport, AgendaEventSupport agendaEventSupport, RuleEventListenerSupport ruleEventListenerSupport, InternalAgenda agenda) {
        super(id, kBase, handleFactory, false, propagationContext, config, environment, workingMemoryEventSupport, agendaEventSupport, ruleEventListenerSupport, agenda);
    }


    public void reset(int handleId,
                      long handleCounter,
                      long propagationCounter) {
        super.reset(handleId, handleCounter, propagationCounter );
        if (liaPropagations != null) liaPropagations.clear();
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        WorkingMemoryEntryPoint ep = this.entryPoints.get(name);
        return ep != null ? new ReteWorkingMemoryEntryPoint( this, ep ) : null;
    }

    public void addLIANodePropagation(LIANodePropagation liaNodePropagation) {
        if (liaPropagations == null) liaPropagations = new ArrayList();
        liaPropagations.add( liaNodePropagation );
    }

    private Integer syncLock = 42;
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

    public void fireUntilHalt(final AgendaFilter agendaFilter) {
        initInitialFact();
        super.fireUntilHalt( agendaFilter );
    }

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
                for ( Iterator it = liaPropagations.iterator(); it.hasNext(); ) {
                    ((LIANodePropagation) it.next()).doPropagation( this );
                }
            }

            // do we need to call this in advance?
            executeQueuedActions();

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

            pCtx.evaluateActionQueue( this );

            getFactHandleFactory().destroyFactHandle( factHandle );

        } finally {
            this.lock.unlock();
            this.kBase.readUnlock();
            endOperation();
        }
    }

    protected BaseNode[] evalQuery(String queryName, DroolsQuery queryObject, InternalFactHandle handle, PropagationContext pCtx) {
        initInitialFact();

        BaseNode[] tnodes = ( BaseNode[] ) kBase.getReteooBuilder().getTerminalNodes(queryName);
        // no need to call retract, as no leftmemory used.
        getEntryPointNode().assertQuery( handle,
                                         pCtx,
                                         this );

        pCtx.evaluateActionQueue( this );
        return tnodes;
    }

}
