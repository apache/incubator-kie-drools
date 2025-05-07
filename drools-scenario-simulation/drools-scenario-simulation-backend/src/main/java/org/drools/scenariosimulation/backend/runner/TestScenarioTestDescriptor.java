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

import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestScenarioTestDescriptor extends AbstractTestDescriptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarioTestDescriptor.class);

    private final ScenarioWithIndex scenarioWithIndex;

    public TestScenarioTestDescriptor(TestDescriptor testSuiteDescriptor,
                                      String fileName,
                                      ScenarioWithIndex scenarioWithIndex) {
        super(testSuiteDescriptor.getUniqueId().append("scenario", String.valueOf(scenarioWithIndex.getIndex())),
                String.format("#%d: %s", scenarioWithIndex.getIndex(), scenarioWithIndex.getScesimData().getDescription()),
                ClassSource.from(fileName));
        this.scenarioWithIndex = scenarioWithIndex;
        setParent(testSuiteDescriptor);
        LOGGER.debug("TestScenarioTestDescriptor created scenario index: {}, fileName: {}", scenarioWithIndex.getIndex(), fileName);
    }

    @Override
    public Type getType() {
        return Type.TEST;
    }

    public ScenarioWithIndex getScenarioWithIndex() {
        return scenarioWithIndex;
    }
}
