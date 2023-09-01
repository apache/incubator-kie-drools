package org.drools.commands.fluent;

import org.drools.commands.RequestContextImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.command.RegistryContext;

public class NewContextCommand<Void> implements ExecutableCommand<Void> {

    private String name;

    public NewContextCommand(String name) {
        this.name = name;
    }

    @Override
    public Void execute(Context context) {
        Context returned = ((RegistryContext) context).getContextManager().createContext(name);
        ((RequestContextImpl) context).setApplicationContext(returned);
        return null;
    }
}
