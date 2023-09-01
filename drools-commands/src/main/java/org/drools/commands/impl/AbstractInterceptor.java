package org.drools.commands.impl;

import org.drools.commands.ChainableRunner;
import org.drools.commands.fluent.PseudoClockRunner;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.RequestContext;

public abstract class AbstractInterceptor extends PseudoClockRunner implements ChainableRunner {

    private ExecutableRunner next;

    public void setNext(ExecutableRunner runner) {
        this.next = runner;
    }

    public ExecutableRunner getNext() {
        return next;
    }

    protected void executeNext( Executable executable, Context ctx ) {
        next.execute(executable, ctx);
    }
}
