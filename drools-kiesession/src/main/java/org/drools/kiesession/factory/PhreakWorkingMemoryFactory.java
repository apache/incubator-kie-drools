package org.drools.kiesession.factory;

import java.io.Serializable;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.runtime.Environment;

public class PhreakWorkingMemoryFactory implements WorkingMemoryFactory, Serializable {

    private static final WorkingMemoryFactory INSTANCE = new PhreakWorkingMemoryFactory();

    public static WorkingMemoryFactory getInstance() {
        return INSTANCE;
    }

    public InternalWorkingMemory createWorkingMemory(long id, InternalKnowledgeBase kBase, SessionConfiguration config, Environment environment) {
        return new StatefulKnowledgeSessionImpl( id, kBase, true, config, environment);
    }

    public InternalWorkingMemory createWorkingMemory(long id, InternalKnowledgeBase kBase, FactHandleFactory handleFactory, long propagationContext, SessionConfiguration config, Environment environment) {
        return new StatefulKnowledgeSessionImpl(id, kBase, handleFactory, propagationContext, config, environment);
    }
}
