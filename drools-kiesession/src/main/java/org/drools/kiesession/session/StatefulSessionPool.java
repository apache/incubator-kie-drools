package org.drools.kiesession.session;

import java.util.function.Supplier;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.util.ScalablePool;

public class StatefulSessionPool {

    private final InternalKnowledgeBase kbase;
    private final ScalablePool<StatefulKnowledgeSessionImpl> pool;

    public StatefulSessionPool(InternalKnowledgeBase kbase, int initialSize, Supplier<StatefulKnowledgeSessionImpl> supplier) {
        this.kbase = kbase;
        this.pool = new ScalablePool<>(initialSize, supplier, s -> s.reset(), s -> s.fromPool(null).dispose());
    }

    public InternalKnowledgeBase getKieBase() {
        return kbase;
    }

    public StatefulKnowledgeSessionImpl get() {
        return pool.get().fromPool( this );
    }

    public void release(StatefulKnowledgeSessionImpl session) {
        pool.release( session );
    }

    public void shutdown() {
        pool.shutdown();
    }
}
