package org.mvel.integration.impl;

import org.mvel.integration.VariableResolver;
import org.mvel.integration.VariableResolverFactory;

import java.util.HashMap;
import java.util.Map;

public class MapVariableResolverFactory implements VariableResolverFactory {
    /**
     * Holds the instance of the variables.
     */
    private Map<String, Object> variables;

    private Map<String, VariableResolver> variableResolvers;
    private VariableResolverFactory nextFactory;


    public MapVariableResolverFactory(Map<String, Object> variables) {
        this.variables = variables;
    }

    public VariableResolver createVariable(String name, Object value) {
        variables.put(name, value);
        return new MapVariableResolver(variables, name);
    }

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


    public boolean isResolveable(String name) {
        if (variableResolvers != null && variableResolvers.containsKey(name)) {
            return true;
        }
        else if (variables != null && variables.containsKey(name)) {
            if (variableResolvers == null) variableResolvers = new HashMap<String, VariableResolver>();
            variableResolvers.put(name, new MapVariableResolver(variables, name));
            return true;
        }
        else if (nextFactory != null) {
            return nextFactory.isResolveable(name);
        }
        return false;
    }

    public void pack() {
        if (variables != null) {
            if (variableResolvers == null) variableResolvers = new HashMap<String,VariableResolver>();
            for (String s : variables.keySet()) {
                variableResolvers.put(s, new MapVariableResolver(variables, s));
            }
        }
    }


    public boolean isTarget(String name) {
        return variableResolvers.containsKey(name);
    }
}
