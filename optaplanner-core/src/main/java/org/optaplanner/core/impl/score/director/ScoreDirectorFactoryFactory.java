/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.director;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenDroolsScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoreDirectorFactoryFactory<Solution_> {

    private static final String GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME = "optaplanner.drools.generateTestOnError";
    private static final Logger logger = LoggerFactory.getLogger(ScoreDirectorFactoryFactory.class);

    private final ScoreDirectorFactoryConfig config;

    public ScoreDirectorFactoryFactory(ScoreDirectorFactoryConfig config) {
        this.config = config;
    }

    public InnerScoreDirectorFactory<Solution_> buildScoreDirectorFactory(ClassLoader classLoader,
            EnvironmentMode environmentMode, SolutionDescriptor<Solution_> solutionDescriptor) {
        AbstractScoreDirectorFactory<Solution_> scoreDirectorFactory =
                decideMultipleScoreDirectorFactories(classLoader, solutionDescriptor);
        if (config.getAssertionScoreDirectorFactory() != null) {
            if (config.getAssertionScoreDirectorFactory().getAssertionScoreDirectorFactory() != null) {
                throw new IllegalArgumentException("A assertionScoreDirectorFactory ("
                        + config.getAssertionScoreDirectorFactory() + ") cannot have a non-null assertionScoreDirectorFactory ("
                        + config.getAssertionScoreDirectorFactory().getAssertionScoreDirectorFactory() + ").");
            }
            if (environmentMode.compareTo(EnvironmentMode.FAST_ASSERT) > 0) {
                throw new IllegalArgumentException("A non-null assertionScoreDirectorFactory ("
                        + config.getAssertionScoreDirectorFactory() + ") requires an environmentMode ("
                        + environmentMode + ") of " + EnvironmentMode.FAST_ASSERT + " or lower.");
            }
            ScoreDirectorFactoryFactory<Solution_> assertionScoreDirectorFactoryFactory =
                    new ScoreDirectorFactoryFactory<>(config.getAssertionScoreDirectorFactory());
            scoreDirectorFactory.setAssertionScoreDirectorFactory(assertionScoreDirectorFactoryFactory
                    .buildScoreDirectorFactory(classLoader, EnvironmentMode.NON_REPRODUCIBLE, solutionDescriptor));
        }
        scoreDirectorFactory.setInitializingScoreTrend(InitializingScoreTrend.parseTrend(
                config.getInitializingScoreTrend() == null ? InitializingScoreTrendLevel.ANY.name()
                        : config.getInitializingScoreTrend(),
                solutionDescriptor.getScoreDefinition().getLevelsSize()));
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            scoreDirectorFactory.setAssertClonedSolution(true);
        }
        return scoreDirectorFactory;
    }

    protected AbstractScoreDirectorFactory<Solution_> decideMultipleScoreDirectorFactories(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor) {
        AbstractScoreDirectorFactory<Solution_> easyScoreDirectorFactory = buildEasyScoreDirectorFactory(solutionDescriptor);
        AbstractScoreDirectorFactory<Solution_> constraintStreamScoreDirectorFactory =
                buildConstraintStreamScoreDirectorFactory(solutionDescriptor);
        AbstractScoreDirectorFactory<Solution_> incrementalScoreDirectorFactory = buildIncrementalScoreDirectorFactory(
                solutionDescriptor);
        AbstractScoreDirectorFactory<Solution_> droolsScoreDirectorFactory = buildDroolsScoreDirectorFactory(
                classLoader, solutionDescriptor);
        if (Stream.of(easyScoreDirectorFactory, constraintStreamScoreDirectorFactory,
                incrementalScoreDirectorFactory, droolsScoreDirectorFactory)
                .filter(Objects::nonNull).count() > 1) {
            List<String> scoreDirectorFactoryPropertyList = new ArrayList<>(4);
            if (easyScoreDirectorFactory != null) {
                scoreDirectorFactoryPropertyList.add("an easyScoreCalculatorClass");
            }
            if (constraintStreamScoreDirectorFactory != null) {
                scoreDirectorFactoryPropertyList.add("a constraintProviderClass");
            }
            if (incrementalScoreDirectorFactory != null) {
                scoreDirectorFactoryPropertyList.add("an incrementalScoreCalculatorClass");
            }
            if (droolsScoreDirectorFactory != null) {
                scoreDirectorFactoryPropertyList.add("a droolsScoreDirectorFactory");
            }
            throw new IllegalArgumentException("The scoreDirectorFactory cannot have "
                    + String.join(" and ", scoreDirectorFactoryPropertyList) + " together.");
        }
        AbstractScoreDirectorFactory<Solution_> scoreDirectorFactory;
        if (easyScoreDirectorFactory != null) {
            scoreDirectorFactory = easyScoreDirectorFactory;
        } else if (constraintStreamScoreDirectorFactory != null) {
            scoreDirectorFactory = constraintStreamScoreDirectorFactory;
        } else if (incrementalScoreDirectorFactory != null) {
            scoreDirectorFactory = incrementalScoreDirectorFactory;
        } else if (droolsScoreDirectorFactory != null) {
            scoreDirectorFactory = droolsScoreDirectorFactory;
        } else {
            throw new IllegalArgumentException("The scoreDirectorFactory lacks a configuration for an "
                    + "easyScoreCalculatorClass, a constraintProviderClass, an incrementalScoreCalculatorClass or a droolsScoreDirectorFactory.");
        }

        return scoreDirectorFactory;
    }

    protected EasyScoreDirectorFactory<Solution_> buildEasyScoreDirectorFactory(
            SolutionDescriptor<Solution_> solutionDescriptor) {
        if (config.getEasyScoreCalculatorClass() != null) {
            if (!EasyScoreCalculator.class.isAssignableFrom(config.getEasyScoreCalculatorClass())) {
                throw new IllegalArgumentException(
                        "The easyScoreCalculatorClass (" + config.getEasyScoreCalculatorClass()
                                + ") does not implement " + EasyScoreCalculator.class.getSimpleName() + ".");
            }
            EasyScoreCalculator<Solution_> easyScoreCalculator = ConfigUtils.newInstance(config,
                    "easyScoreCalculatorClass", config.getEasyScoreCalculatorClass());
            ConfigUtils.applyCustomProperties(easyScoreCalculator, "easyScoreCalculatorClass",
                    config.getEasyScoreCalculatorCustomProperties(), "easyScoreCalculatorCustomProperties");
            return new EasyScoreDirectorFactory<>(solutionDescriptor, easyScoreCalculator);
        } else {
            if (config.getEasyScoreCalculatorCustomProperties() != null) {
                throw new IllegalStateException(
                        "If there is no easyScoreCalculatorClass (" + config.getEasyScoreCalculatorClass()
                                + "), then there can be no easyScoreCalculatorCustomProperties ("
                                + config.getEasyScoreCalculatorCustomProperties() + ") either.");
            }
            return null;
        }
    }

    protected ConstraintStreamScoreDirectorFactory<Solution_> buildConstraintStreamScoreDirectorFactory(
            SolutionDescriptor<Solution_> solutionDescriptor) {
        if (config.getConstraintProviderClass() != null) {
            if (!ConstraintProvider.class.isAssignableFrom(config.getConstraintProviderClass())) {
                throw new IllegalArgumentException(
                        "The constraintProviderClass (" + config.getConstraintProviderClass()
                                + ") does not implement " + ConstraintProvider.class.getSimpleName() + ".");
            }
            ConstraintProvider constraintProvider = ConfigUtils.newInstance(config,
                    "constraintProviderClass", config.getConstraintProviderClass());
            ConfigUtils.applyCustomProperties(constraintProvider, "constraintProviderClass",
                    config.getConstraintProviderCustomProperties(), "constraintProviderCustomProperties");
            ConstraintStreamImplType constraintStreamImplType_ = defaultIfNull(config.getConstraintStreamImplType(),
                    ConstraintStreamImplType.DROOLS);
            return new ConstraintStreamScoreDirectorFactory<>(solutionDescriptor, constraintProvider,
                    constraintStreamImplType_);
        } else {
            if (config.getConstraintProviderCustomProperties() != null) {
                throw new IllegalStateException("If there is no constraintProviderClass (" + config.getConstraintProviderClass()
                        + "), then there can be no constraintProviderCustomProperties ("
                        + config.getConstraintProviderCustomProperties() + ") either.");
            }
            return null;
        }
    }

    protected IncrementalScoreDirectorFactory<Solution_> buildIncrementalScoreDirectorFactory(
            SolutionDescriptor<Solution_> solutionDescriptor) {
        if (config.getIncrementalScoreCalculatorClass() != null) {
            if (!IncrementalScoreCalculator.class.isAssignableFrom(config.getIncrementalScoreCalculatorClass())) {
                throw new IllegalArgumentException(
                        "The incrementalScoreCalculatorClass (" + config.getIncrementalScoreCalculatorClass()
                                + ") does not implement " + IncrementalScoreCalculator.class.getSimpleName() + ".");
            }
            return new IncrementalScoreDirectorFactory<>(solutionDescriptor, config.getIncrementalScoreCalculatorClass(),
                    config.getIncrementalScoreCalculatorCustomProperties());
        } else {
            if (config.getIncrementalScoreCalculatorCustomProperties() != null) {
                throw new IllegalStateException(
                        "If there is no incrementalScoreCalculatorClass (" + config.getIncrementalScoreCalculatorClass()
                                + "), then there can be no incrementalScoreCalculatorCustomProperties ("
                                + config.getIncrementalScoreCalculatorCustomProperties() + ") either.");
            }
            return null;
        }
    }

    protected DroolsScoreDirectorFactory<Solution_> buildDroolsScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor) {
        boolean generateDroolsTestOnError =
                Boolean.parseBoolean(System.getProperty(GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME, "false"));

        if (ConfigUtils.isEmptyCollection(config.getScoreDrlList())
                && ConfigUtils.isEmptyCollection(config.getScoreDrlFileList())) {
            if (config.getKieBaseConfigurationProperties() != null) {
                throw new IllegalArgumentException(
                        "If kieBaseConfigurationProperties (" + config.getKieBaseConfigurationProperties()
                                + ") is not null, the scoreDrlList (" + config.getScoreDrlList()
                                + ") or the scoreDrlFileList (" + config.getScoreDrlFileList() + ") must not be empty.");
            }
            if (generateDroolsTestOnError) {
                throw new IllegalArgumentException(
                        "If " + GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME + " system property (" +
                                generateDroolsTestOnError + ") is set, the scoreDrlList (" + config.getScoreDrlList()
                                + ") or the scoreDrlFileList (" + config.getScoreDrlFileList() + ") must not be empty.");
            }
            return null;
        }

        KieServices kieServices = KieServices.Factory.get();
        KieResources kieResources = kieServices.getResources();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        writeScoreDrlListToKieFileSystem(kieFileSystem, kieResources, classLoader);
        writeScoreDrlFileListToKieFileSystem(kieFileSystem, kieResources);

        // Can be overwritten by kieBaseConfigurationProperties
        KieModuleModel kmodel = kieServices.newKieModuleModel()
                .setConfigurationProperty(PropertySpecificOption.PROPERTY_NAME,
                        PropertySpecificOption.ALLOWED.toString());
        kieFileSystem.writeKModuleXML(kmodel.toXML());

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException("There are errors in a score DRL:\n" + results);
        } else if (results.hasMessages(Message.Level.WARNING)) {
            logger.warn("There are warnings in a score DRL:\n{}", results);
        }
        KieContainer kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());

        KieBaseConfiguration kieBaseConfiguration = buildKieBaseConfiguration(kieServices);
        KieBase kieBase = kieContainer.newKieBase(kieBaseConfiguration);
        if (generateDroolsTestOnError) {
            return new TestGenDroolsScoreDirectorFactory<>(solutionDescriptor, kieBase, config.getScoreDrlList(),
                    config.getScoreDrlFileList());
        } else {
            return new DroolsScoreDirectorFactory<>(solutionDescriptor, kieBase);
        }
    }

    private void writeScoreDrlListToKieFileSystem(KieFileSystem kieFileSystem, KieResources kieResources,
            ClassLoader classLoader) {
        if (!ConfigUtils.isEmptyCollection(config.getScoreDrlList())) {
            ClassLoader actualClassLoader = (classLoader != null) ? classLoader : getClass().getClassLoader();
            for (String scoreDrl : config.getScoreDrlList()) {
                if (scoreDrl == null) {
                    throw new IllegalArgumentException("The scoreDrl (" + scoreDrl + ") cannot be null.");
                }
                URL scoreDrlURL = actualClassLoader.getResource(scoreDrl);
                if (scoreDrlURL == null) {
                    String errorMessage = "The scoreDrl (" + scoreDrl + ") does not exist as a classpath resource"
                            + " in the classLoader (" + actualClassLoader + ").";
                    if (scoreDrl.startsWith("/")) {
                        errorMessage += "\nAs from 6.1, a classpath resource should not start with a slash (/)."
                                + " A scoreDrl now adheres to ClassLoader.getResource(String)."
                                + " Remove the leading slash from the scoreDrl if you're upgrading from 6.0.";
                    }
                    throw new IllegalArgumentException(errorMessage);
                }
                kieFileSystem.write(kieResources.newClassPathResource(scoreDrl, "UTF-8", actualClassLoader));
            }
        }
    }

    private void writeScoreDrlFileListToKieFileSystem(KieFileSystem kieFileSystem, KieResources kieResources) {
        if (!ConfigUtils.isEmptyCollection(config.getScoreDrlFileList())) {
            for (File scoreDrlFile : config.getScoreDrlFileList()) {
                if (scoreDrlFile == null) {
                    throw new IllegalArgumentException("The scoreDrlFile (" + scoreDrlFile + ") cannot be null.");
                }
                if (!scoreDrlFile.exists()) {
                    throw new IllegalArgumentException("The scoreDrlFile (" + scoreDrlFile
                            + ") does not exist.");
                }
                kieFileSystem.write(kieResources.newFileSystemResource(scoreDrlFile, "UTF-8"));
            }
        }
    }

    protected KieBaseConfiguration buildKieBaseConfiguration(KieServices kieServices) {
        KieBaseConfiguration kieBaseConfiguration = kieServices.newKieBaseConfiguration();
        if (config.getKieBaseConfigurationProperties() != null) {
            for (Map.Entry<String, String> entry : config.getKieBaseConfigurationProperties().entrySet()) {
                kieBaseConfiguration.setProperty(entry.getKey(), entry.getValue());
            }
        }
        return kieBaseConfiguration;
    }
}
