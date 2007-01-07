package org.mvel.integration.impl;

import org.mvel.integration.VariableResolver;
import org.mvel.integration.VariableResolverFactory;

import java.util.Map;

/**
 * Use this class to extend you own VariableResolverFactories.
 */
public abstract class BaseVariableResolver implements VariableResolverFactory {
    protected Map<String, VariableResolver> variableResolvers;
    protected VariableResolverFactory nextFactory;

    public VariableResolverFactory getNextFactory() {
        return nextFactory;
    }

    public VariableResolverFactory setNextFactory(VariableResolverFactory resolverFactory) {
        return nextFactory = resolverFactory;
    }

    public VariableResolver getVariableResolver(String name) {
        return isResolveable(name) ? variableResolvers.get(name) :
                nextFactory != null ? nextFactory.getVariableResolver(name) : null;
    }

}
