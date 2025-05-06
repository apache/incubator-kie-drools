/*
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
package org.drools.scenariosimulation.backend.runner;

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.utils.ConstantsHolder;
import org.drools.scenariosimulation.backend.expression.ExpressionEvaluatorFactory;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerDTO;
import org.drools.scenariosimulation.backend.runner.model.ScenarioRunnerData;
import org.drools.scenariosimulation.backend.util.ScenarioSimulationXMLPersistence;
import org.junit.platform.commons.support.AnnotationSupport;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class TestScenarioEngine implements TestEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestScenarioEngine.class);
    private static final String TEST_ENGINE_ID = "kie-test-scenario";
    private static final String TEST_ENGINE_DESCRIPTION = "Apache KIE Test Scenario engine";
    private static final ScenarioSimulationXMLPersistence XML_READER = ScenarioSimulationXMLPersistence.getInstance();

    @Override
    public String getId() {
        return TEST_ENGINE_ID;
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        LOGGER.debug("Starting {} with id: {}", TEST_ENGINE_DESCRIPTION, TEST_ENGINE_ID);
        LOGGER.debug("Discovering scesim files for Test Scenario Engine");

        EngineDescriptor engineDescriptor = new EngineDescriptor(uniqueId, TEST_ENGINE_DESCRIPTION);

        discoveryRequest.getSelectorsByType(ClassSelector.class).forEach(selector ->
            appendTestSuites(selector.getJavaClass(), engineDescriptor)
        );

        LOGGER.debug("Discovered {} scesim files", engineDescriptor.getChildren().size());
        return engineDescriptor;
    }

    private void appendTestSuites(Class<?> javaClass, TestDescriptor engineDescriptor) {
        if (AnnotationSupport.isAnnotated(javaClass, TestScenarioActivator.class)) {
            LOGGER.debug("A class with @TestScenarioActivator annotation found: {}", javaClass.getCanonicalName());

            getScesimFileAbsolutePaths().stream().map(TestScenarioEngine::parseFile).forEach(scesim -> {
                String fileName = getScesimFileName(scesim.getFileName());
                TestScenarioTestSuiteDescriptor suite = new TestScenarioTestSuiteDescriptor(engineDescriptor, fileName, scesim.getSettings().getType());
                engineDescriptor.addChild(suite);
                scesim.getScenarioWithIndices().forEach(scenarioWithIndex ->
                    suite.addChild(new TestScenarioTestDescriptor(suite, fileName, scesim, scenarioWithIndex))
                );
            });
        }
    }

    static List<String> getScesimFileAbsolutePaths() {
        try (Stream<Path> fileStream = Files.walk(Paths.get("."))) {
            LOGGER.debug("Scanning Test Scenario (*.scesim) files");
            List<String> scesimFileAbsolutePaths = fileStream.filter(TestScenarioEngine::filterResource)
                    .map(Path::normalize)
                    .map(Path::toFile)
                    .map(File::getAbsolutePath)
                    .toList();

            LOGGER.debug("Found Test Scenario (*.scesim) {} files", scesimFileAbsolutePaths.size());
            scesimFileAbsolutePaths.forEach(LOGGER::debug);

            return scesimFileAbsolutePaths;
        } catch (IOException e) {
            LOGGER.error("Error scanning Test Scenario (*.scesim)", e);
            return Collections.emptyList();
        }
    }

    static String getScesimFileName(String fileFullPath) {
        int idx = fileFullPath.replace("\\", "/").lastIndexOf('/');
        String fileName = idx >= 0 ? fileFullPath.substring(idx + 1) : fileFullPath;
        return fileName.endsWith(ConstantsHolder.SCESIM_EXTENSION) ? fileName.substring(0, fileName.lastIndexOf(ConstantsHolder.SCESIM_EXTENSION)) : fileName;
    }

    static boolean filterResource(Path path) {
        return path.toString().endsWith(".scesim") && !path.toAbsolutePath().toString().replace("\\", "/").contains("/target/") && Files.isRegularFile(path);
    }

    static ScenarioRunnerDTO parseFile(String path) {
        try (final Scanner scanner = new Scanner(new File(path))) {
            String rawFile = scanner.useDelimiter("\\Z").next();
            ScenarioSimulationModel scenarioSimulationModel = XML_READER.unmarshal(rawFile);
            return new ScenarioRunnerDTO(scenarioSimulationModel, path);
        } catch (FileNotFoundException e) {
            throw new ScenarioException("File not found, this should not happen: " + path, e);
        } catch (Exception e) {
            throw new ScenarioException("Issue on parsing file: " + path, e);
        }
    }

    @Override
    public void execute(ExecutionRequest executionRequest) {
        LOGGER.debug("Executing {} Test Scenarios", executionRequest.getRootTestDescriptor().getChildren().size());

        EngineExecutionListener listener = executionRequest.getEngineExecutionListener();
        executionRequest.getRootTestDescriptor().getChildren().stream()
                .map(TestScenarioTestSuiteDescriptor.class::cast)
                .forEach(testSuiteDescriptor -> {
                    LOGGER.debug("Executing {} Test Scenario which contains {} scenarios", testSuiteDescriptor.getDisplayName(), testSuiteDescriptor.getChildren().size());
                    listener.executionStarted(testSuiteDescriptor);
                    AbstractRunnerHelper runnerHelper = getRunnerHelper(testSuiteDescriptor.getTestScenarioType());

                    testSuiteDescriptor.getChildren().stream()
                            .map(TestScenarioTestDescriptor.class::cast)
                            .forEach(testDescriptor -> {
                                listener.executionStarted(testDescriptor);

                                ScenarioRunnerDTO scenarioRunnerDTO = testDescriptor.getScenarioRunnerDTO();
                                ExpressionEvaluatorFactory expressionEvaluatorFactory = ExpressionEvaluatorFactory.create(
                                        getKieContainer().getClassLoader(),
                                        scenarioRunnerDTO.getSettings().getType());
                                ScesimModelDescriptor simulationModelDescriptor = scenarioRunnerDTO.getSimulationModelDescriptor();
                                Settings settings = scenarioRunnerDTO.getSettings();
                                Background background = scenarioRunnerDTO.getBackground();
                                ScenarioWithIndex scenarioWithIndex = testDescriptor.getScenarioWithIndex();
                                ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();

                                try {
                                    runnerHelper.run(getKieContainer(),
                                            simulationModelDescriptor,
                                            scenarioWithIndex,
                                            expressionEvaluatorFactory,
                                            getKieContainer().getClassLoader(),
                                            scenarioRunnerData,
                                            settings,
                                            background);
                                } catch (Exception e) {
                                    listener.executionFinished(testDescriptor, TestExecutionResult.failed(e));
                                    LOGGER.error(e.getMessage(), e);
                                }

                                listener.executionFinished(testDescriptor, TestExecutionResult.successful());
                            });

                    /** TODO Test results depends on children results **/
                    listener.executionFinished(testSuiteDescriptor, TestExecutionResult.successful());
                    /***** END **/
                });
    }

    /**
     * Temporary hack, it is needed because AbstractScenarioRunner invokes kieContainer.getClassLoader() in the constructor
     *
     * @return
     */
    private static KieContainer mockKieContainer() {
        InvocationHandler nullHandler = (o, method, objects) -> null;
        return (KieContainer) Proxy.newProxyInstance(KieContainer.class.getClassLoader(),
                new Class[] { KieContainer.class }, nullHandler);
    }

    KieContainer getKieContainer() {
        return KieServices.get().getKieClasspathContainer();
    }

    static AbstractRunnerHelper getRunnerHelper(ScenarioSimulationModel.Type type) {
        if (ScenarioSimulationModel.Type.RULE.equals(type)) {
            return new RuleScenarioRunnerHelper();
        } else if (ScenarioSimulationModel.Type.DMN.equals(type)) {
            return new DMNScenarioRunnerHelper();
        } else {
            throw new IllegalArgumentException("Impossible to run simulation of type " + type);
        }
    }

}
