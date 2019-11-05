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

package org.drools.scenariosimulation.backend.runner;

import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.kie.api.runtime.KieContainer;

public class DMNScenarioRunner extends AbstractScenarioRunner {

    public DMNScenarioRunner(KieContainer kieContainer, ScenarioRunnerDTO scenarioRunnerDTO) {
        super(kieContainer,
              scenarioRunnerDTO,
              ExpressionEvaluatorFactory.create(
                      kieContainer.getClassLoader(),
                      scenarioRunnerDTO.getSettings().getType()));
    }

    @Override
    protected AbstractRunnerHelper newRunnerHelper() {
        return new DMNScenarioRunnerHelper();
    }
}
