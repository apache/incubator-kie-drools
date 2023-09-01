package org.drools.commands.fluent;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.builder.fluent.KieContainerFluent;
import org.kie.internal.builder.fluent.KieSessionFluent;

public class FluentComponentFactory {

    private Map<String, Class> fluents;
    private Map<String, String> fluentTargets;

    public FluentComponentFactory() {
        fluents = new HashMap<>();
        fluentTargets = new HashMap<>();

        set(KieContainerFluent.class.getName(), KieContainerFluentImpl.class, KieContainer.class.getName());
        set(KieSessionFluent.class.getName(), KieSessionFluentImpl.class, KieSession.class.getName());
        set(ExecutableBuilder.class.getName(), ExecutableBuilderImpl.class, null);
    }

    public void set(String fluentType, Class fluentImpl, String fluentTarget) {
        fluents.put(fluentType, fluentImpl);

        if (fluentTarget != null) {
            // only BatchBuilderFluent is currently null
            fluentTargets.put(fluentType, fluentTarget);
        }
    }

    public Class getImplClass(String type) {
        return fluents.get(type);
    }

    public String getFluentTarget(String fluentType) {
        return fluentTargets.get(fluentType);
    }
}
