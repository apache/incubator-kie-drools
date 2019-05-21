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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.backend.runner.model.SimulationWithFileName;
import org.drools.scenariosimulation.backend.util.ScenarioSimulationXMLPersistence;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import static org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils.FILE_EXTENSION;
import static org.drools.scenariosimulation.backend.util.ResourceHelper.getResourcesByExtension;

public class ScenarioJunitActivator extends ParentRunner<SimulationWithFileName> {

    public static final String ACTIVATOR_CLASS_NAME = "ScenarioJunitActivatorTest";

    public static final Function<String, String> ACTIVATOR_CLASS_CODE = modulePackage ->
            String.format("package %s;\n/**\n* Do not remove this file\n*/\n@%s(%s.class)\npublic class %s {\n}",
                          modulePackage,
                          RunWith.class.getCanonicalName(),
                          ScenarioJunitActivator.class.getCanonicalName(),
                          ScenarioJunitActivator.ACTIVATOR_CLASS_NAME);

    public ScenarioJunitActivator(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected List<SimulationWithFileName> getChildren() {
        return getResources().map(this::parseFile)
                .filter(this::isNotSkipFromBuild)
                .collect(Collectors.toList());
    }

    @Override
    protected Description describeChild(SimulationWithFileName child) {
        return AbstractScenarioRunner.getDescriptionForSimulation(Optional.of(child.getFileName()), child.getSimulation());
    }

    @Override
    protected void runChild(SimulationWithFileName child, RunNotifier notifier) {
        KieContainer kieClasspathContainer = getKieContainer();
        AbstractScenarioRunner scenarioRunner = newRunner(kieClasspathContainer, child.getSimulation(), child.getFileName());
        scenarioRunner.run(notifier);
    }

    protected SimulationWithFileName parseFile(String path) {
        try {
            String rawFile = new Scanner(new File(path)).useDelimiter("\\Z").next();
            return new SimulationWithFileName(getXmlReader().unmarshal(rawFile).getSimulation(), path);
        } catch (FileNotFoundException e) {
            throw new ScenarioException("File not found, this should not happen: " + path, e);
        }
    }

    protected boolean isNotSkipFromBuild(SimulationWithFileName simulationWithFileName) {
        return !simulationWithFileName.getSimulation().getSimulationDescriptor().isSkipFromBuild();
    }

    ScenarioSimulationXMLPersistence getXmlReader() {
        return ScenarioSimulationXMLPersistence.getInstance();
    }

    Stream<String> getResources() {
        return getResourcesByExtension(FILE_EXTENSION);
    }

    KieContainer getKieContainer() {
        return KieServices.get().getKieClasspathContainer();
    }

    AbstractScenarioRunner newRunner(KieContainer kieContainer, Simulation simulation, String fileName) {
        AbstractScenarioRunner runner = AbstractScenarioRunner.getSpecificRunnerProvider(simulation.getSimulationDescriptor())
                .create(kieContainer, simulation.getSimulationDescriptor(), simulation.getScenarioWithIndex());
        runner.setFileName(fileName);
        return runner;
    }
}
