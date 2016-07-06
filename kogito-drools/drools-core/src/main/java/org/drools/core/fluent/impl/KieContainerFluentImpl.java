package org.drools.core.fluent.impl;

import org.drools.core.command.NewKieSessionCommand;
import org.kie.internal.fluent.runtime.FluentBuilder;
import org.kie.internal.fluent.runtime.KieContainerFluent;
import org.kie.internal.fluent.runtime.KieSessionFluent;

public class KieContainerFluentImpl extends BaseBatchFluent<FluentBuilder, FluentBuilder> implements KieContainerFluent {

    private ExecutableImpl ctx;

    public KieContainerFluentImpl(ExecutableImpl ctx) {
        super(ctx);
        this.ctx = ctx;
    }
    @Override
    public KieSessionFluent newSession() {
        return newSession(null);
    }

    @Override
    public KieSessionFluent newSession(String sessionId) {
        NewKieSessionCommand cmd = new NewKieSessionCommand(sessionId);
        ctx.addCommand(cmd);
        return new KieSessionFluentImpl(ctx);
    }


}
