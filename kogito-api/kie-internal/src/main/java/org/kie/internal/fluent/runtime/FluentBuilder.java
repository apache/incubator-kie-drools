package org.kie.internal.fluent.runtime;

import org.kie.api.builder.ReleaseId;
import org.kie.internal.fluent.ContextFluent;
import org.kie.internal.fluent.Executable;

public interface FluentBuilder extends TimeFluent<FluentBuilder>, ContextFluent<FluentBuilder, FluentBuilder> {

    KieContainerFluent getKieContainer(ReleaseId releaseId);

    Executable getExecutable();
}
