/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kogito.scenariosimulation.runner;

import org.drools.scenariosimulation.backend.runner.AbstractRunnerHelper;
import org.drools.scenariosimulation.backend.runner.DMNScenarioRunner;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.kie.api.runtime.KieContainer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class KogitoDMNScenarioRunner extends DMNScenarioRunner {

    private static final KieContainer mockKieContainer = mockKieContainer();

    public KogitoDMNScenarioRunner(ScenarioRunnerDTO scenarioRunnerDTO) {
        super(mockKieContainer, scenarioRunnerDTO);
    }

    @Override
    protected AbstractRunnerHelper newRunnerHelper() {
        return new KogitoDMNScenarioRunnerHelper();
    }

    /**
     * Temporary hack, it is needed because AbstractScenarioRunner invokes kieContainer.getClassLoader() in the constructor
     * @return
     */
    private static KieContainer mockKieContainer() {
        InvocationHandler nullHandler = (o, method, objects) -> null;
        return (KieContainer) Proxy.newProxyInstance(KieContainer.class.getClassLoader(),
                new Class[] { KieContainer.class }, nullHandler);
    }
}
