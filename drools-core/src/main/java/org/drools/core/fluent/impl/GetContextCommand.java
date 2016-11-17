package org.drools.core.fluent.impl;


import org.drools.core.command.RequestContextImpl;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.kie.api.runtime.Context;

public class GetContextCommand<Void> implements ExecutableCommand<Void> {
    private String name;

    public GetContextCommand(String name) {
        this.name = name;
    }

    @Override
    public Void execute(Context context) {
        Context returned = ( (RegistryContext) context ).getContextManager().getContext( name );
        ((RequestContextImpl)context).setApplicationContext(returned);
        return null;
    }

    @Override
    public String toString() {
        return "GetContextCommand{" +
               "name='" + name + '\'' +
               '}';
    }
}
