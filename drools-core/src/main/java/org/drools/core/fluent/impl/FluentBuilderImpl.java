package org.drools.core.fluent.impl;

import org.drools.core.command.GetKieContainerCommand;
import org.kie.api.builder.ReleaseId;
import org.kie.internal.fluent.Executable;
import org.kie.internal.fluent.runtime.FluentBuilder;
import org.kie.internal.fluent.runtime.KieContainerFluent;

public class FluentBuilderImpl extends BaseBatchFluent<FluentBuilder, FluentBuilder> implements FluentBuilder {


    public FluentBuilderImpl() {
        super(new ExecutableImpl());
        getFluentContext().setFluentBuilder(this);

    }

    @Override
    public KieContainerFluent getKieContainer(ReleaseId releaseId) {
        addCommand( new GetKieContainerCommand(releaseId) );
        KieContainerFluentImpl fluent = new KieContainerFluentImpl(fluentCtx);
        return fluent;
    }

    @Override
    public Executable getExecutable() {
        return getFluentContext();
    }
}
