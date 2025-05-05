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
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestScenarioTestDescriptor extends AbstractTestDescriptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarioTestDescriptor.class);

    private final ScenarioRunnerDTO scenarioRunnerDTO;
    private final int index;
    private final String fileName;
    private final ScenarioWithIndex scenarioWithIndex;

    public TestScenarioTestDescriptor(UniqueId uniqueId, String fileName, int index, ScenarioRunnerDTO scenarioRunnerDTO, ScenarioWithIndex scenarioWithIndex) {
        super(uniqueId.append("scenario", fileName.concat(String.valueOf(index))),
                String.valueOf(index).concat(fileName),
                ClassSource.from(TestScenarioEngine.class));
        this.scenarioRunnerDTO = scenarioRunnerDTO;
        this.index = index;
        this.fileName = fileName;
        this.scenarioWithIndex = scenarioWithIndex;
        LOGGER.info("TestScenarioTestDescriptor created index: {}, fileName: {}", index, fileName);
    }

    @Override
    public Type getType() {
        return Type.TEST;
    }

    public ScenarioRunnerDTO getScenarioRunnerDTO() {
        return scenarioRunnerDTO;
    }

    public int getIndex() {
        return index;
    }

    public String getFileName() {
        return fileName;
    }

    public ScenarioWithIndex getScenarioWithIndex() {
        return scenarioWithIndex;
    }
}
