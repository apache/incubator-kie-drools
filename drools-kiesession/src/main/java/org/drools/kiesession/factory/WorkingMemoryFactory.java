package org.drools.kiesession.factory;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.runtime.Environment;

public interface WorkingMemoryFactory {
    InternalWorkingMemory createWorkingMemory(final long id,
                                              final InternalKnowledgeBase kBase,
                                              final SessionConfiguration config,
                                              final Environment environment);

    InternalWorkingMemory createWorkingMemory(final long id,
                                              final InternalKnowledgeBase kBase,
                                              final FactHandleFactory handleFactory,
                                              final long propagationContext,
                                              final SessionConfiguration config,
                                              final Environment environment);
}

