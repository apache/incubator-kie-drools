package org.drools.commands.fluent;

import org.drools.commands.RequestContextImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.builder.fluent.Scope;

public class GetCommand<T> implements ExecutableCommand<T> {

    private String name;
    private Scope scope;

    public GetCommand(String name) {
        this.name = name;
    }

    public GetCommand(String name, Scope scope) {
        this.name = name;
        this.scope = scope;
    }

    @Override
    public T execute(Context context) {
        RequestContextImpl reqContext = (RequestContextImpl) context;

        T object = null;
        if (reqContext.has(name)) {
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
