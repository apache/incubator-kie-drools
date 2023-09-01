package org.drools.commands.fluent;

import org.drools.commands.GetKieContainerCommand;
import org.drools.commands.SetKieContainerCommand;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.builder.fluent.KieContainerFluent;

public class ExecutableBuilderImpl extends BaseBatchFluent<ExecutableBuilder, ExecutableBuilder> implements ExecutableBuilder {

    public ExecutableBuilderImpl() {
        super(new ExecutableImpl());
        getFluentContext().setExecutableBuilder(this);
    }

    @Override
    public KieContainerFluent getKieContainer(ReleaseId releaseId) {
        addCommand(new GetKieContainerCommand(releaseId));
        KieContainerFluentImpl fluent = new KieContainerFluentImpl(fluentCtx);
        return fluent;
    }

    @Override
    public KieContainerFluent setKieContainer(KieContainer kieContainer) {
        addCommand(new SetKieContainerCommand(kieContainer));
        KieContainerFluentImpl fluent = new KieContainerFluentImpl(fluentCtx);
        return fluent;
    }

    @Override
    public Executable getExecutable() {
        return getFluentContext();
    }
}
