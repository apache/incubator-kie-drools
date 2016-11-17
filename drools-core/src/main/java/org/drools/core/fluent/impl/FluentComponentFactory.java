/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
