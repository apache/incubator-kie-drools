package org.drools.core.common;

import org.drools.core.SessionConfiguration;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.WorkingMemoryEventSupport;
import org.drools.core.spi.FactHandleFactory;
import org.kie.api.runtime.Environment;

import java.io.Serializable;

public class PhreakWorkingMemoryFactory implements WorkingMemoryFactory, Serializable {

    public InternalWorkingMemory createWorkingMemory(int id, InternalRuleBase ruleBase, SessionConfiguration config, Environment environment) {
        return new AbstractWorkingMemory(id, ruleBase, config, environment);
    }

    public InternalWorkingMemory createWorkingMemory(int id, InternalRuleBase ruleBase, FactHandleFactory handleFactory, InternalFactHandle initialFactHandle, long propagationContext, SessionConfiguration config, InternalAgenda agenda, Environment environment) {
        return new AbstractWorkingMemory(id, ruleBase, handleFactory, initialFactHandle, propagationContext, config, agenda, environment);
    }

    public InternalWorkingMemory createWorkingMemory(int id, InternalRuleBase ruleBase, FactHandleFactory handleFactory, InternalFactHandle initialFactHandle, long propagationContext, SessionConfiguration config, Environment environment, WorkingMemoryEventSupport workingMemoryEventSupport, AgendaEventSupport agendaEventSupport, RuleEventListenerSupport ruleEventListenerSupport, InternalAgenda agenda) {
        return new AbstractWorkingMemory(id, ruleBase, handleFactory, initialFactHandle, propagationContext, config, environment, workingMemoryEventSupport, agendaEventSupport, ruleEventListenerSupport, agenda);
    }
}
