package org.drools.reliability.core;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.kiesession.factory.PhreakWorkingMemoryFactory;
import org.drools.kiesession.factory.WorkingMemoryFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.runtime.Environment;

public class ReliablePhreakWorkingMemoryFactory extends PhreakWorkingMemoryFactory {

    private static final WorkingMemoryFactory INSTANCE = new ReliablePhreakWorkingMemoryFactory();

    public static WorkingMemoryFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public InternalWorkingMemory createWorkingMemory(long id, InternalKnowledgeBase kBase, SessionConfiguration config, Environment environment) {
        return new ReliableStatefulKnowledgeSessionImpl( id, kBase, true, config, environment);
    }

    @Override
    public InternalWorkingMemory createWorkingMemory(long id, InternalKnowledgeBase kBase, FactHandleFactory handleFactory, long propagationContext, SessionConfiguration config, Environment environment) {
        return new ReliableStatefulKnowledgeSessionImpl(id, kBase, handleFactory, propagationContext, config, environment);
    }
}
