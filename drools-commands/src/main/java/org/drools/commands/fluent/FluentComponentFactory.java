/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
