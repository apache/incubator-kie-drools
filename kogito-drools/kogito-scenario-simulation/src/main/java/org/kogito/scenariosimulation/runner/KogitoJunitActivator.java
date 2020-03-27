/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kogito.scenariosimulation.runner;

import org.drools.scenariosimulation.backend.runner.ScenarioJunitActivator;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class KogitoJunitActivator extends ScenarioJunitActivator {

    public KogitoJunitActivator(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected void runChild(ScenarioRunnerDTO child, RunNotifier notifier) {
        KogitoDMNScenarioRunner scenarioRunner = new KogitoDMNScenarioRunner(child);
        scenarioRunner.run(notifier);
    }
}