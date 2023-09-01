package org.drools.commands.fluent;

import java.util.Map;

import org.drools.commands.impl.ContextImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;

public class SetVarAsRegistryEntry<Void> implements ExecutableCommand<Void> {

    private String registryName;
    private String varName;

    public SetVarAsRegistryEntry(String registryName, String varName) {
        this.registryName = registryName;
        this.varName = varName;
    }

    @Override
    public Void execute(Context context) {
        Object o = context.get(varName);

        ((Map<String, Object>) context.get(ContextImpl.REGISTRY)).put(registryName, o);
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
