package org.drools.core.fluent.impl;


import org.drools.core.command.RequestContextImpl;
import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;

public class GetContextCommand<Void> implements GenericCommand<Void> {
    private String name;

    public GetContextCommand(String name) {
        this.name = name;
    }

    @Override
    public Void execute(Context context) {
        Context returned = context.getContextManager().getContext(name);
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
