package org.kie.internal.builder.fluent;

import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.utils.ClassLoaderUtil;

public interface ExecutableBuilder extends TimeFluent<ExecutableBuilder>,
                                           ContextFluent<ExecutableBuilder, ExecutableBuilder> {

    KieContainerFluent getKieContainer(ReleaseId releaseId);

    KieContainerFluent setKieContainer(KieContainer kieContainer);

    Executable getExecutable();

    static ExecutableBuilder create() {
        try {
            return (ExecutableBuilder) ClassLoaderUtil.getClassLoader(null, null, true)
                    .loadClass("org.drools.commands.fluent.ExecutableBuilderImpl")
                    .newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance ExecutableRunner, please add org.drools:drools-commands to your classpath", e);
        }
    }
}
