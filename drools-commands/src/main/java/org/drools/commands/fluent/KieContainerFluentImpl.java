package org.drools.commands.fluent;

import java.util.function.BiFunction;

import org.drools.commands.NewKieSessionCommand;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.builder.fluent.KieContainerFluent;
import org.kie.internal.builder.fluent.KieSessionFluent;

public class KieContainerFluentImpl extends BaseBatchFluent<ExecutableBuilder, ExecutableBuilder> implements KieContainerFluent {

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

    @Override
    public KieSessionFluent newSessionCustomized(String sessionId, BiFunction<String, KieContainer, KieSessionConfiguration> kieSessionConfigurationCustomizer) {
        NewKieSessionCommand cmd = new NewKieSessionCommand(sessionId);
        cmd.setCustomizeSessionConfiguration(kieSessionConfigurationCustomizer);
        ctx.addCommand(cmd);
        return new KieSessionFluentImpl(ctx);
    }
}
