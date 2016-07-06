package org.drools.core.fluent.impl;

import org.drools.core.command.RequestContextImpl;
import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;
import org.kie.internal.fluent.Scope;

public class GetCommand<T> implements GenericCommand<T> {
    private String name;
    private Scope scope;

    public GetCommand(String name) {
        this.name = name;
    }

    public  GetCommand(String name, Scope scope) {
        this.name = name;
        this.scope = scope;
    }

    @Override
    public T execute(Context context) {
        RequestContextImpl reqContext = (RequestContextImpl)context;

        T object = null;
        if ( reqContext.has(name)) {
            object = (T) reqContext.get(name);
            reqContext.setLastSetOrGet(name);
        }

        return object;
    }

    @Override
    public String toString() {
        return "SetCommand{" +
               "name='" + name + '\'' +
               ", scope=" + scope +
               '}';
    }
}
