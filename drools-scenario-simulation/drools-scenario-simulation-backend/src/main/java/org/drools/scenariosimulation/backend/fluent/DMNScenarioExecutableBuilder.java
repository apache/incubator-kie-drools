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
package org.drools.scenariosimulation.backend.fluent;

import org.drools.scenariosimulation.backend.util.DMNSimulationUtils;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.api.runtime.RequestContext;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

public class DMNScenarioExecutableBuilder {

    public static final String DEFAULT_APPLICATION = "defaultApplication";
    public static final String DMN_RESULT = "dmnResult";
    public static final String DMN_MODEL = "dmnModel";

    private final DMNRuntime dmnRuntime;
    private final DMNContext dmnContext;
    private DMNModel dmnModel;

    private DMNScenarioExecutableBuilder(KieContainer kieContainer, String applicationName) {
        dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);
        dmnContext = dmnRuntime.newContext();
    }

    private DMNScenarioExecutableBuilder(KieContainer kieContainer) {
        this(kieContainer, DEFAULT_APPLICATION);
    }

    public static DMNScenarioExecutableBuilder createBuilder(KieContainer kieContainer, String applicationName) {
        return new DMNScenarioExecutableBuilder(kieContainer, applicationName);
    }

    public static DMNScenarioExecutableBuilder createBuilder(KieContainer kieContainer) {
        return new DMNScenarioExecutableBuilder(kieContainer);
    }

    public void setActiveModel(String path) {
        dmnModel = DMNSimulationUtils.extractDMNModel(dmnRuntime, path);
    }

    public void setValue(String key, Object value) {
        dmnContext.set(key, value);
    }

    public RequestContext run() {
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModel, dmnContext);
        RequestContext requestContext = ExecutableRunner.create().createContext();
        requestContext.setResult(dmnResult);
        requestContext.setOutput(DMN_MODEL, dmnModel);
        requestContext.setOutput(DMN_RESULT, dmnResult);
        return requestContext;
    }
}
