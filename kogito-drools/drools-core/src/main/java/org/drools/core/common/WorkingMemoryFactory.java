package org.drools.core.common;

import org.drools.core.SessionConfiguration;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.WorkingMemoryEventSupport;
import org.drools.core.spi.FactHandleFactory;
import org.kie.api.runtime.Environment;

public interface WorkingMemoryFactory {
    InternalWorkingMemory createWorkingMemory(final int id,
                                              final InternalRuleBase ruleBase,
                                              final SessionConfiguration config,
                                              final Environment environment);

    InternalWorkingMemory createWorkingMemory(final int id,
                                              final InternalRuleBase ruleBase,
                                              final FactHandleFactory handleFactory,
                                              final InternalFactHandle initialFactHandle,
                                              final long propagationContext,
                                              final SessionConfiguration config,
                                              final InternalAgenda agenda,
                                              final Environment environment);

    InternalWorkingMemory createWorkingMemory(final int id,
                                              final InternalRuleBase ruleBase,
                                              final FactHandleFactory handleFactory,
                                              final InternalFactHandle initialFactHandle,
                                              final long propagationContext,
                                              final SessionConfiguration config,
                                              final Environment environment,
                                              final WorkingMemoryEventSupport workingMemoryEventSupport,
                                              final AgendaEventSupport agendaEventSupport,
                                              final RuleEventListenerSupport ruleEventListenerSupport,
                                              final InternalAgenda agenda);
}

