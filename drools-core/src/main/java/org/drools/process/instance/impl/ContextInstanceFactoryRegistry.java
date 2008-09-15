package org.drools.process.instance.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.Context;
import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.core.context.swimlane.SwimlaneContext;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.exception.DefaultExceptionScopeInstance;
import org.drools.process.instance.context.swimlane.SwimlaneContextInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.process.instance.impl.factory.ReuseContextInstanceFactory;

public class ContextInstanceFactoryRegistry {
    
    public static final ContextInstanceFactoryRegistry INSTANCE = 
        new ContextInstanceFactoryRegistry();

    private Map<Class<? extends Context>, ContextInstanceFactory> registry;

    public ContextInstanceFactoryRegistry() {
        this.registry = new HashMap<Class<? extends Context>, ContextInstanceFactory>();

        // hard wired contexts:
        register(VariableScope.class, new ReuseContextInstanceFactory(
                 VariableScopeInstance.class));
        register(ExceptionScope.class, new ReuseContextInstanceFactory(
                 DefaultExceptionScopeInstance.class));
        register(SwimlaneContext.class, new ReuseContextInstanceFactory(
                 SwimlaneContextInstance.class));
    }

    public void register(Class<? extends Context> cls,
            ContextInstanceFactory factory) {
        this.registry.put(cls, factory);
    }

    public ContextInstanceFactory getContextInstanceFactory(Context context) {
        return this.registry.get(context.getClass());
    }
}
