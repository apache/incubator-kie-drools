package org.drools.core.fluent.impl;


import org.kie.internal.fluent.runtime.FluentBuilder;
import org.kie.internal.fluent.runtime.KieContainerFluent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.fluent.runtime.KieSessionFluent;

import java.util.HashMap;
import java.util.Map;

public class FluentComponentFactory {
    private Map<String, Class> fluents;
    private Map<String, String> fluentTargets;

    public FluentComponentFactory() {
        fluents = new HashMap<String, Class>();
        fluentTargets = new HashMap<String, String>();

        set(KieContainerFluent.class.getName(), KieContainerFluentImpl.class, KieContainer.class.getName());
        set(KieSessionFluent.class.getName(), KieSessionFluentImpl.class, KieSession.class.getName());
        set(FluentBuilder.class.getName(), FluentBuilderImpl.class, null);

    }

    public void set(String fluentType, Class fluentImpl, String fluentTarget) {
        fluents.put(fluentType, fluentImpl);

        if ( fluentTargets != null ) {
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
