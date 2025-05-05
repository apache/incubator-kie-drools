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
            appendTestsInClass(selector.getJavaClass(), engineDescriptor)
        );

        LOGGER.debug("Discovered {} scesim files", engineDescriptor.getChildren().size());
        return engineDescriptor;
    }

    private void appendTestsInClass(Class<?> javaClass, TestDescriptor engineDescriptor) {
        if (AnnotationSupport.isAnnotated(javaClass, TestScenarioActivator.class)) {
            LOGGER.debug("A class with @TestScenario annotation found: {}", javaClass.getCanonicalName());

            getScesimFileAbsolutePaths().stream().map(this::parseFile).forEach(scesim -> {
                String fileName = getScesimFileName(scesim.getFileName());
                TestScenarioTestSuiteDescriptor suite = new TestScenarioTestSuiteDescriptor(fileName, engineDescriptor);
                engineDescriptor.addChild(suite);
                for (ScenarioWithIndex scenarioWithIndex : scesim.getScenarioWithIndices()) {
                    int index = scenarioWithIndex.getIndex();
                    suite.addChild(new TestScenarioTestDescriptor(engineDescriptor.getUniqueId(), fileName, index, scesim, scenarioWithIndex));
                }
            });
        }
    }

    List<String> getScesimFileAbsolutePaths() {
        try (Stream<Path> fileStream = Files.walk(Paths.get("."))) {
            LOGGER.debug("Scanning Test Scenario (*.scesim) files");
            List<String> scesimFileAbsolutePaths = fileStream.filter(this::filterResource)
                    .map(Path::normalize)
                    .map(Path::toFile)
                    .map(File::getAbsolutePath)
                    .toList();

            LOGGER.debug("Found Test Scenario (*.scesim) {} files", scesimFileAbsolutePaths.size());
            scesimFileAbsolutePaths.forEach(LOGGER::info);

            return scesimFileAbsolutePaths;
        } catch (IOException e) {
            LOGGER.error("Error scanning Test Scenario (*.scesim)", e);
            return Collections.emptyList();
        }
    }

    boolean filterResource(Path path) {
        return path.toString().endsWith(".scesim") && !path.toString().contains("/target/") && Files.isRegularFile(path);
    }

    /*
     * public static Description getDescriptionForSimulation(Optional<String> fullFileName, List<ScenarioWithIndex> scenarios) {
     * String testSuiteName = fullFileName.isPresent() ? getScesimFileName(fullFileName.get()) : AbstractScenarioRunner.class.getSimpleName();
     * Description suiteDescription = Description.createSuiteDescription(testSuiteName);
     * scenarios.forEach(scenarioWithIndex -> suiteDescription.addChild(
     * getDescriptionForScenario(fullFileName,
     * scenarioWithIndex.getIndex(),
     * scenarioWithIndex.getScesimData().getDescription())));
     * return suiteDescription;
     * }
     * 
     * public static Description getDescriptionForScenario(Optional<String> fullFileName, int index, String description) {
     * String testName = fullFileName.isPresent() ? getScesimFileName(fullFileName.get()) : AbstractScenarioRunner.class.getSimpleName();
     * return Description.createTestDescription(testName,
     * String.format("#%d: %s", index, description));
     * }
     */

    public static String getScesimFileName(String fileFullPath) {
        if (fileFullPath == null) {
            return null;
        }
        int idx = fileFullPath.replace("\\", "/").lastIndexOf('/');
        String fileName = idx >= 0 ? fileFullPath.substring(idx + 1) : fileFullPath;
        return fileName.endsWith(ConstantsHolder.SCESIM_EXTENSION) ? fileName.substring(0, fileName.lastIndexOf(ConstantsHolder.SCESIM_EXTENSION)) : fileName;
    }

    @Override
    public void execute(ExecutionRequest executionRequest) {
        LOGGER.info("Executing {} Test Scenarios", executionRequest.getRootTestDescriptor().getChildren().size());
        EngineExecutionListener listener = executionRequest.getEngineExecutionListener();
        DMNScenarioRunnerHelper scenarioRunnerHelper = new DMNScenarioRunnerHelper();
        RuleScenarioRunnerHelper ruleScenarioRunnerHelper = new RuleScenarioRunnerHelper();


        for (TestDescriptor testSuiteDescriptor : executionRequest.getRootTestDescriptor().getChildren()) {
            System.out.println("SUb Tests " + testSuiteDescriptor.getChildren().size() + " name: " + testSuiteDescriptor.getDisplayName());

            listener.executionStarted(testSuiteDescriptor);
            TestExecutionResult state = TestExecutionResult.successful();
            for (TestDescriptor testDescriptor : testSuiteDescriptor.getChildren()) {
                listener.executionStarted(testDescriptor);

                ScenarioRunnerDTO scenarioRunnerDTO = ((TestScenarioTestDescriptor) testDescriptor).getScenarioRunnerDTO();
                ExpressionEvaluatorFactory expressionEvaluatorFactory = ExpressionEvaluatorFactory.create(
                        getKieContainer().getClassLoader(),
                        scenarioRunnerDTO.getSettings().getType());
                ScesimModelDescriptor simulationModelDescriptor = scenarioRunnerDTO.getSimulationModelDescriptor();
                Settings settings = scenarioRunnerDTO.getSettings();
                Background background = scenarioRunnerDTO.getBackground();
                //System.out.println("test descriptor " + testDescriptor.getDisplayName());
                ScenarioWithIndex scenarioWithIndex = ((TestScenarioTestDescriptor) testDescriptor).getScenarioWithIndex();
                ScenarioRunnerData scenarioRunnerData = new ScenarioRunnerData();




                try {
                    //var r = getSpecificRunnerProvider(scenarioRunnerDTO.getSettings().getType()).create(getKieContainer(), scenarioRunnerDTO);

                    scenarioRunnerHelper.run(getKieContainer(),
                            simulationModelDescriptor,
                            scenarioWithIndex,
                            expressionEvaluatorFactory,
                            getKieContainer().getClassLoader(),
                            scenarioRunnerData,
                            settings,
                            background);
                } catch (ScenarioException e) {
                    /*
                     * IndexedScenarioException indexedScenarioException = new IndexedScenarioException(index, e);
                     * indexedScenarioException.setFileName(scenarioRunnerDTO.getFileName());
                     */
                    //                    runNotifier.fireTestFailure(new Failure(descriptionForScenario, indexedScenarioException));
                    listener.executionFinished(testDescriptor, TestExecutionResult.failed(e));
                    //listener.executionFinished(testDescriptorEngine, TestExecutionResult.failed(e));


                    LOGGER.error(e.getMessage(), e);
                } catch (Throwable e) {
                    /*
                     * IndexedScenarioException indexedScenarioException = new IndexedScenarioException(index, "Unexpected test error in scenario '" +
                     * scenarioWithIndex.getScesimData().getDescription() + "'", e);
                     * indexedScenarioException.setFileName(scenarioRunnerDTO.getFileName());
                     */
                   listener.executionFinished(testDescriptor, TestExecutionResult.failed(e));
                    //listener.executionFinished(testDescriptorEngine, TestExecutionResult.failed(e));
                    LOGGER.error(e.getMessage(), e);
                    //                    runNotifier.fireTestFailure(new Failure(descriptionForScenario, indexedScenarioException));
                }

                listener.executionFinished(testDescriptor, TestExecutionResult.successful());

                //System.out.println(testDescriptor.getDisplayName() + " Succcess");
            }

            //System.out.println("Suite finished with " + state.toString());
            listener.executionFinished(testSuiteDescriptor, state);
        }

    }

    protected ScenarioRunnerDTO parseFile(String path) {
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

    public static ScenarioRunnerProvider getSpecificRunnerProvider(ScenarioSimulationModel.Type type) {
        if (ScenarioSimulationModel.Type.RULE.equals(type)) {
            return RuleScenarioRunner::new;
        } else if (ScenarioSimulationModel.Type.DMN.equals(type)) {
            return DMNScenarioRunner::new;
        } else {
            throw new IllegalArgumentException("Impossible to run simulation of type " + type);
        }
    }

}
