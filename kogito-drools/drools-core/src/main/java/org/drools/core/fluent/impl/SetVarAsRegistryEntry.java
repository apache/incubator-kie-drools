package org.drools.core.fluent.impl;


import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;

import java.util.Map;

public class SetVarAsRegistryEntry<Void> implements GenericCommand<Void> {
    private String registryName;
    private String varName;

    public SetVarAsRegistryEntry(String registryName, String varName) {
        this.registryName = registryName;
        this.varName = varName;
    }

    @Override
    public Void execute(Context context) {
        Object o = context.get(varName);

        ((Map<String, Object>)context.get(ContextImpl.REGISTRY)).put(registryName, o);
        return null;
    }

    @Override
    public String toString() {
        return "SetVarAsRegistryEntry{" +
               "registryName='" + registryName + '\'' +
               ", varName='" + varName + '\'' +
               '}';
    }
}
