package org.drools.core.command;

import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;

import java.util.Map;


public class OutCommand<T> implements GenericCommand<T> {
    private String name;

    public OutCommand() {
    }

    public OutCommand(String name) {
        this.name = name;
    }

    @Override
    public T execute(Context context) {
        T returned = (T) ((RequestContextImpl)context).getLastReturned();

        String actualName;
        if ( this.name != null ) {
            actualName = this.name;
        } else {
            actualName = ((RequestContextImpl)context).getLastSet();
            if ( actualName == null ) {
                throw new RuntimeException("Name was null and there was no last set name either");
            }
        }

        ((RequestContextImpl)context).getOut().put(actualName, returned);

        return returned;
    }
}
