package org.drools.commands.fluent;

import org.drools.commands.RequestContextImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.command.RegistryContext;

public class GetContextCommand<Void> implements ExecutableCommand<Void> {

    private String name;

    public GetContextCommand(String name) {
        this.name = name;
    }

    @Override
    public Void execute(Context context) {
        Context returned = ((RegistryContext) context).getContextManager().getContext(name);
        ((RequestContextImpl) context).setApplicationContext(returned);
        return null;
    }

    @Override
    public String toString() {
        return "GetContextCommand{" +
                "name='" + name + '\'' +
                '}';
    }
}
