package org.drools.core.command;

import org.drools.core.command.impl.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.RequestContext;


public class OutCommand<T> implements ExecutableCommand<T> {
    private String name;

    public OutCommand() {
    }

    public OutCommand(String name) {
        this.name = name;
    }

    @Override
    public T execute(Context context) {
        T returned = (T) ((RequestContext)context).getResult();

        String actualName;
        if ( this.name != null ) {
            actualName = this.name;
        } else {
            actualName = ((RequestContextImpl)context).getLastSet();
            if ( actualName == null ) {
                throw new RuntimeException("Name was null and there was no last set name either");
            }
        }

        context.set(actualName, returned);

        return returned;
    }
}
