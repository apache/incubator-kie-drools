package org.drools.process.instance;

import java.util.HashMap;
import java.util.Map;

import org.drools.knowledge.definitions.process.Process;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.instance.RuleFlowProcessInstanceFactory;

public class ProcessInstanceFactoryRegistry {
    
    public static final ProcessInstanceFactoryRegistry instance =
        new ProcessInstanceFactoryRegistry();

    private Map<Class< ? extends Process>, ProcessInstanceFactory> registry;

    public ProcessInstanceFactoryRegistry() {
        this.registry = new HashMap<Class< ? extends Process>, ProcessInstanceFactory>();

        // hard wired nodes:
        register( RuleFlowProcess.class,
                  new RuleFlowProcessInstanceFactory() );
    }

    public void register(Class< ? extends Process> cls,
                         ProcessInstanceFactory factory) {
        this.registry.put( cls,
                           factory );
    }

    public ProcessInstanceFactory getProcessInstanceFactory(Process process) {
        return this.registry.get( process.getClass() );
    }
}
