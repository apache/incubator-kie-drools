/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scenariosimulation.backend.runner;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestScenarioTestSuiteDescriptor extends AbstractTestDescriptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarioTestSuiteDescriptor.class);

    private final ScenarioSimulationModel.Type testScenarioType;

    public TestScenarioTestSuiteDescriptor(TestDescriptor engineDescriptor,
                                           String fileName,
                                           ScenarioSimulationModel.Type testScenarioType) {
        super(engineDescriptor.getUniqueId().append("scesim", fileName),
                fileName,
                ClassSource.from(fileName));
        setParent(engineDescriptor);
        this.testScenarioType = testScenarioType;
        LOGGER.debug("TestScenarioSuite created fileName: {} and added in {}", fileName, engineDescriptor.getDisplayName());
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    public ScenarioSimulationModel.Type getTestScenarioType() {
        return testScenarioType;
    }
}
