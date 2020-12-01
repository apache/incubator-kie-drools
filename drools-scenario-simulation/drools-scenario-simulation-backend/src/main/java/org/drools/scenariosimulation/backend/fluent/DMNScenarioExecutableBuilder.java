/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.scenariosimulation.backend.fluent;

import java.util.Objects;

import org.drools.scenariosimulation.backend.util.DMNSimulationUtils;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.builder.fluent.DMNRuntimeFluent;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.command.RegistryContext;

public class DMNScenarioExecutableBuilder {

    public static final String DEFAULT_APPLICATION = "defaultApplication";
    public static final String DMN_RESULT = "dmnResult";
    public static final String DMN_MODEL = "dmnModel";

    private final DMNRuntimeFluent dmnRuntimeFluent;
    private final ExecutableBuilder executableBuilder;

    private DMNScenarioExecutableBuilder(KieContainer kieContainer, String applicationName) {
        executableBuilder = ExecutableBuilder.create();

        dmnRuntimeFluent = executableBuilder.newApplicationContext(applicationName)
                .setKieContainer(kieContainer)
                .newDMNRuntime();
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
        dmnRuntimeFluent
                .addCommand(context -> {
                    RegistryContext registryContext = (RegistryContext) context;

                    DMNRuntime dmnRuntime = registryContext.lookup(DMNRuntime.class);
                    if (dmnRuntime == null) {
                        throw new IllegalStateException("There is no DMNRuntime available");
                    }

                    DMNModel dmnModel = DMNSimulationUtils.extractDMNModel(dmnRuntime, path);
                    registryContext.register(DMNModel.class, dmnModel);
                    return dmnModel;
                })
                .out(DMN_MODEL);
    }

    public void setValue(String key, Object value) {
        dmnRuntimeFluent.setInput(key, value);
    }

    public RequestContext run() {
        Objects.requireNonNull(executableBuilder, "Executable builder is null, please invoke create(KieContainer, )");

        dmnRuntimeFluent
                .evaluateModel()
                .out(DMN_RESULT)
                .end();

        return ExecutableRunner.create().execute(executableBuilder.getExecutable());
    }
}
