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

package org.optaplanner.core.config.score.director;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.config.util.KeyAsElementMapConverter;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.drools.testgen.TestGenDroolsScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.optaplanner.core.impl.score.director.easy.EasyScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;
import org.optaplanner.core.impl.util.JaxbCustomPropertiesAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("scoreDirectorFactory")
public class ScoreDirectorFactoryConfig extends AbstractConfig<ScoreDirectorFactoryConfig> {

    private static final Logger logger = LoggerFactory.getLogger(ScoreDirectorFactoryConfig.class);
    private static final String GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME = "optaplanner.drools.generateTestOnError";

    protected Class<? extends EasyScoreCalculator> easyScoreCalculatorClass = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    @XStreamConverter(KeyAsElementMapConverter.class)
    protected Map<String, String> easyScoreCalculatorCustomProperties = null;

    protected Class<? extends ConstraintProvider> constraintProviderClass = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    @XStreamConverter(KeyAsElementMapConverter.class)
    protected Map<String, String> constraintProviderCustomProperties = null;
    protected ConstraintStreamImplType constraintStreamImplType;

    protected Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    @XStreamConverter(KeyAsElementMapConverter.class)
    protected Map<String, String> incrementalScoreCalculatorCustomProperties = null;

    @XmlElement(name = "scoreDrl")
    @XStreamImplicit(itemFieldName = "scoreDrl")
    protected List<String> scoreDrlList = null;
    @XmlElement(name = "scoreDrlFile")
    @XStreamImplicit(itemFieldName = "scoreDrlFile")
    protected List<File> scoreDrlFileList = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    @XStreamConverter(KeyAsElementMapConverter.class)
    protected Map<String, String> kieBaseConfigurationProperties = null;

    // TODO: this should be rather an enum?
    protected String initializingScoreTrend = null;

    @XmlElement(name = "assertionScoreDirectorFactory")
    @XStreamAlias("assertionScoreDirectorFactory")
    protected ScoreDirectorFactoryConfig assertionScoreDirectorFactory = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public Class<? extends EasyScoreCalculator> getEasyScoreCalculatorClass() {
        return easyScoreCalculatorClass;
    }

    public void setEasyScoreCalculatorClass(Class<? extends EasyScoreCalculator> easyScoreCalculatorClass) {
        this.easyScoreCalculatorClass = easyScoreCalculatorClass;
    }

    public Map<String, String> getEasyScoreCalculatorCustomProperties() {
        return easyScoreCalculatorCustomProperties;
    }

    public void setEasyScoreCalculatorCustomProperties(Map<String, String> easyScoreCalculatorCustomProperties) {
        this.easyScoreCalculatorCustomProperties = easyScoreCalculatorCustomProperties;
    }

    public Class<? extends ConstraintProvider> getConstraintProviderClass() {
        return constraintProviderClass;
    }

    public void setConstraintProviderClass(Class<? extends ConstraintProvider> constraintProviderClass) {
        this.constraintProviderClass = constraintProviderClass;
    }

    public Map<String, String> getConstraintProviderCustomProperties() {
        return constraintProviderCustomProperties;
    }

    public void setConstraintProviderCustomProperties(Map<String, String> constraintProviderCustomProperties) {
        this.constraintProviderCustomProperties = constraintProviderCustomProperties;
    }

    public ConstraintStreamImplType getConstraintStreamImplType() {
        return constraintStreamImplType;
    }

    public void setConstraintStreamImplType(ConstraintStreamImplType constraintStreamImplType) {
        this.constraintStreamImplType = constraintStreamImplType;
    }

    public Class<? extends IncrementalScoreCalculator> getIncrementalScoreCalculatorClass() {
        return incrementalScoreCalculatorClass;
    }

    public void setIncrementalScoreCalculatorClass(
            Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass) {
        this.incrementalScoreCalculatorClass = incrementalScoreCalculatorClass;
    }

    public Map<String, String> getIncrementalScoreCalculatorCustomProperties() {
        return incrementalScoreCalculatorCustomProperties;
    }

    public void setIncrementalScoreCalculatorCustomProperties(Map<String, String> incrementalScoreCalculatorCustomProperties) {
        this.incrementalScoreCalculatorCustomProperties = incrementalScoreCalculatorCustomProperties;
    }

    public List<String> getScoreDrlList() {
        return scoreDrlList;
    }

    public void setScoreDrlList(List<String> scoreDrlList) {
        this.scoreDrlList = scoreDrlList;
    }

    public List<File> getScoreDrlFileList() {
        return scoreDrlFileList;
    }

    public void setScoreDrlFileList(List<File> scoreDrlFileList) {
        this.scoreDrlFileList = scoreDrlFileList;
    }

    public Map<String, String> getKieBaseConfigurationProperties() {
        return kieBaseConfigurationProperties;
    }

    public void setKieBaseConfigurationProperties(Map<String, String> kieBaseConfigurationProperties) {
        this.kieBaseConfigurationProperties = kieBaseConfigurationProperties;
    }

    public String getInitializingScoreTrend() {
        return initializingScoreTrend;
    }

    public void setInitializingScoreTrend(String initializingScoreTrend) {
        this.initializingScoreTrend = initializingScoreTrend;
    }

    public ScoreDirectorFactoryConfig getAssertionScoreDirectorFactory() {
        return assertionScoreDirectorFactory;
    }

    public void setAssertionScoreDirectorFactory(ScoreDirectorFactoryConfig assertionScoreDirectorFactory) {
        this.assertionScoreDirectorFactory = assertionScoreDirectorFactory;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public ScoreDirectorFactoryConfig withEasyScoreCalculatorClass(
            Class<? extends EasyScoreCalculator> easyScoreCalculatorClass) {
        this.easyScoreCalculatorClass = easyScoreCalculatorClass;
        return this;
    }

    public ScoreDirectorFactoryConfig withEasyScoreCalculatorCustomProperties(
            Map<String, String> easyScoreCalculatorCustomProperties) {
        this.easyScoreCalculatorCustomProperties = easyScoreCalculatorCustomProperties;
        return this;
    }

    public ScoreDirectorFactoryConfig withConstraintProviderClass(Class<? extends ConstraintProvider> constraintProviderClass) {
        this.constraintProviderClass = constraintProviderClass;
        return this;
    }

    public ScoreDirectorFactoryConfig withConstraintProviderCustomProperties(
            Map<String, String> constraintProviderCustomProperties) {
        this.constraintProviderCustomProperties = constraintProviderCustomProperties;
        return this;
    }

    public ScoreDirectorFactoryConfig withConstraintStreamImplType(ConstraintStreamImplType constraintStreamImplType) {
        this.constraintStreamImplType = constraintStreamImplType;
        return this;
    }

    public ScoreDirectorFactoryConfig withIncrementalScoreCalculatorClass(
            Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass) {
        this.incrementalScoreCalculatorClass = incrementalScoreCalculatorClass;
        return this;
    }

    public ScoreDirectorFactoryConfig withIncrementalScoreCalculatorCustomProperties(
            Map<String, String> incrementalScoreCalculatorCustomProperties) {
        this.incrementalScoreCalculatorCustomProperties = incrementalScoreCalculatorCustomProperties;
        return this;
    }

    public ScoreDirectorFactoryConfig withScoreDrlList(List<String> scoreDrlList) {
        this.scoreDrlList = scoreDrlList;
        return this;
    }

    public ScoreDirectorFactoryConfig withScoreDrls(String... scoreDrls) {
        this.scoreDrlList = Arrays.asList(scoreDrls);
        return this;
    }

    public ScoreDirectorFactoryConfig withScoreDrlFileList(List<File> scoreDrlFileList) {
        this.scoreDrlFileList = scoreDrlFileList;
        return this;
    }

    public ScoreDirectorFactoryConfig withScoreDrlFiles(File... scoreDrlFiles) {
        this.scoreDrlFileList = Arrays.asList(scoreDrlFiles);
        return this;
    }

    public ScoreDirectorFactoryConfig withInitializingScoreTrend(String initializingScoreTrend) {
        this.initializingScoreTrend = initializingScoreTrend;
        return this;
    }

    public ScoreDirectorFactoryConfig withAssertionScoreDirectorFactory(
            ScoreDirectorFactoryConfig assertionScoreDirectorFactory) {
        this.assertionScoreDirectorFactory = assertionScoreDirectorFactory;
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public <Solution_> InnerScoreDirectorFactory<Solution_> buildScoreDirectorFactory(ClassLoader classLoader,
            EnvironmentMode environmentMode, SolutionDescriptor<Solution_> solutionDescriptor) {
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
        if (assertionScoreDirectorFactory != null) {
            if (assertionScoreDirectorFactory.getAssertionScoreDirectorFactory() != null) {
                throw new IllegalArgumentException("A assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory + ") cannot have a non-null assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory.getAssertionScoreDirectorFactory() + ").");
            }
            if (environmentMode.compareTo(EnvironmentMode.FAST_ASSERT) > 0) {
                throw new IllegalArgumentException("A non-null assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory + ") requires an environmentMode ("
                        + environmentMode + ") of " + EnvironmentMode.FAST_ASSERT + " or lower.");
            }
            scoreDirectorFactory.setAssertionScoreDirectorFactory(
                    assertionScoreDirectorFactory.buildScoreDirectorFactory(classLoader,
                            EnvironmentMode.NON_REPRODUCIBLE, solutionDescriptor));
        }
        scoreDirectorFactory.setInitializingScoreTrend(InitializingScoreTrend.parseTrend(
                initializingScoreTrend == null ? InitializingScoreTrendLevel.ANY.name() : initializingScoreTrend,
                solutionDescriptor.getScoreDefinition().getLevelsSize()));
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            scoreDirectorFactory.setAssertClonedSolution(true);
        }
        return scoreDirectorFactory;
    }

    protected <Solution_> EasyScoreDirectorFactory<Solution_> buildEasyScoreDirectorFactory(
            SolutionDescriptor<Solution_> solutionDescriptor) {
        if (easyScoreCalculatorClass != null) {
            if (!EasyScoreCalculator.class.isAssignableFrom(easyScoreCalculatorClass)) {
                throw new IllegalArgumentException(
                        "The easyScoreCalculatorClass (" + easyScoreCalculatorClass
                                + ") does not implement " + EasyScoreCalculator.class.getSimpleName() + ".");
            }
            EasyScoreCalculator<Solution_> easyScoreCalculator = ConfigUtils.newInstance(this,
                    "easyScoreCalculatorClass", easyScoreCalculatorClass);
            ConfigUtils.applyCustomProperties(easyScoreCalculator, "easyScoreCalculatorClass",
                    easyScoreCalculatorCustomProperties, "easyScoreCalculatorCustomProperties");
            return new EasyScoreDirectorFactory<>(solutionDescriptor, easyScoreCalculator);
        } else {
            if (easyScoreCalculatorCustomProperties != null) {
                throw new IllegalStateException("If there is no easyScoreCalculatorClass (" + easyScoreCalculatorClass
                        + "), then there can be no easyScoreCalculatorCustomProperties ("
                        + easyScoreCalculatorCustomProperties + ") either.");
            }
            return null;
        }
    }

    protected <Solution_> ConstraintStreamScoreDirectorFactory<Solution_> buildConstraintStreamScoreDirectorFactory(
            SolutionDescriptor<Solution_> solutionDescriptor) {
        if (constraintProviderClass != null) {
            if (!ConstraintProvider.class.isAssignableFrom(constraintProviderClass)) {
                throw new IllegalArgumentException(
                        "The constraintProviderClass (" + constraintProviderClass
                                + ") does not implement " + ConstraintProvider.class.getSimpleName() + ".");
            }
            ConstraintProvider constraintProvider = ConfigUtils.newInstance(this,
                    "constraintProviderClass", this.constraintProviderClass);
            ConfigUtils.applyCustomProperties(constraintProvider, "constraintProviderClass",
                    constraintProviderCustomProperties, "constraintProviderCustomProperties");
            ConstraintStreamImplType constraintStreamImplType_ = defaultIfNull(constraintStreamImplType,
                    ConstraintStreamImplType.DROOLS);
            return new ConstraintStreamScoreDirectorFactory<>(solutionDescriptor, constraintProvider,
                    constraintStreamImplType_);
        } else {
            if (constraintProviderCustomProperties != null) {
                throw new IllegalStateException("If there is no constraintProviderClass (" + constraintProviderClass
                        + "), then there can be no constraintProviderCustomProperties ("
                        + constraintProviderCustomProperties + ") either.");
            }
            return null;
        }
    }

    protected <Solution_> IncrementalScoreDirectorFactory<Solution_> buildIncrementalScoreDirectorFactory(
            SolutionDescriptor<Solution_> solutionDescriptor) {
        if (incrementalScoreCalculatorClass != null) {
            if (!IncrementalScoreCalculator.class.isAssignableFrom(incrementalScoreCalculatorClass)) {
                throw new IllegalArgumentException(
                        "The incrementalScoreCalculatorClass (" + incrementalScoreCalculatorClass
                                + ") does not implement " + IncrementalScoreCalculator.class.getSimpleName() + ".");
            }
            return new IncrementalScoreDirectorFactory<>(solutionDescriptor, incrementalScoreCalculatorClass,
                    incrementalScoreCalculatorCustomProperties);
        } else {
            if (incrementalScoreCalculatorCustomProperties != null) {
                throw new IllegalStateException(
                        "If there is no incrementalScoreCalculatorClass (" + incrementalScoreCalculatorClass
                                + "), then there can be no incrementalScoreCalculatorCustomProperties ("
                                + incrementalScoreCalculatorCustomProperties + ") either.");
            }
            return null;
        }
    }

    protected <Solution_> DroolsScoreDirectorFactory<Solution_> buildDroolsScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor) {
        boolean generateDroolsTestOnError =
                Boolean.parseBoolean(System.getProperty(GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME, "false"));
        if (!ConfigUtils.isEmptyCollection(scoreDrlList) || !ConfigUtils.isEmptyCollection(scoreDrlFileList)) {
            KieServices kieServices = KieServices.Factory.get();
            KieResources kieResources = kieServices.getResources();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            if (!ConfigUtils.isEmptyCollection(scoreDrlList)) {
                ClassLoader actualClassLoader = (classLoader != null) ? classLoader : getClass().getClassLoader();
                for (String scoreDrl : scoreDrlList) {
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
            if (!ConfigUtils.isEmptyCollection(scoreDrlFileList)) {
                for (File scoreDrlFile : scoreDrlFileList) {
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

            KieBaseConfiguration kieBaseConfiguration = kieServices.newKieBaseConfiguration();
            if (kieBaseConfigurationProperties != null) {
                for (Map.Entry<String, String> entry : kieBaseConfigurationProperties.entrySet()) {
                    kieBaseConfiguration.setProperty(entry.getKey(), entry.getValue());
                }
            }
            KieBase kieBase = kieContainer.newKieBase(kieBaseConfiguration);
            if (generateDroolsTestOnError) {
                return new TestGenDroolsScoreDirectorFactory<>(solutionDescriptor, kieBase, scoreDrlList, scoreDrlFileList);
            } else {
                return new DroolsScoreDirectorFactory<>(solutionDescriptor, kieBase);
            }
        } else {
            if (kieBaseConfigurationProperties != null) {
                throw new IllegalArgumentException(
                        "If kieBaseConfigurationProperties (" + kieBaseConfigurationProperties
                                + ") is not null, the scoreDrlList (" + scoreDrlList
                                + ") or the scoreDrlFileList (" + scoreDrlFileList + ") must not be empty.");
            }
            if (generateDroolsTestOnError) {
                throw new IllegalArgumentException(
                        "If " + GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME + " system property (" +
                                generateDroolsTestOnError + ") is set, the scoreDrlList (" + scoreDrlList
                                + ") or the scoreDrlFileList (" + scoreDrlFileList + ") must not be empty.");
            }
            return null;
        }
    }

    @Override
    public ScoreDirectorFactoryConfig inherit(ScoreDirectorFactoryConfig inheritedConfig) {
        easyScoreCalculatorClass = ConfigUtils.inheritOverwritableProperty(
                easyScoreCalculatorClass, inheritedConfig.getEasyScoreCalculatorClass());
        easyScoreCalculatorCustomProperties = ConfigUtils.inheritMergeableMapProperty(
                easyScoreCalculatorCustomProperties, inheritedConfig.getEasyScoreCalculatorCustomProperties());
        constraintProviderClass = ConfigUtils.inheritOverwritableProperty(
                constraintProviderClass, inheritedConfig.getConstraintProviderClass());
        constraintProviderCustomProperties = ConfigUtils.inheritMergeableMapProperty(
                constraintProviderCustomProperties, inheritedConfig.getConstraintProviderCustomProperties());
        constraintStreamImplType = ConfigUtils.inheritOverwritableProperty(
                constraintStreamImplType, inheritedConfig.getConstraintStreamImplType());
        incrementalScoreCalculatorClass = ConfigUtils.inheritOverwritableProperty(
                incrementalScoreCalculatorClass, inheritedConfig.getIncrementalScoreCalculatorClass());
        incrementalScoreCalculatorCustomProperties = ConfigUtils.inheritMergeableMapProperty(
                incrementalScoreCalculatorCustomProperties, inheritedConfig.getIncrementalScoreCalculatorCustomProperties());
        scoreDrlList = ConfigUtils.inheritMergeableListProperty(
                scoreDrlList, inheritedConfig.getScoreDrlList());
        scoreDrlFileList = ConfigUtils.inheritMergeableListProperty(
                scoreDrlFileList, inheritedConfig.getScoreDrlFileList());
        kieBaseConfigurationProperties = ConfigUtils.inheritMergeableMapProperty(
                kieBaseConfigurationProperties, inheritedConfig.getKieBaseConfigurationProperties());
        initializingScoreTrend = ConfigUtils.inheritOverwritableProperty(
                initializingScoreTrend, inheritedConfig.getInitializingScoreTrend());

        assertionScoreDirectorFactory = ConfigUtils.inheritOverwritableProperty(
                assertionScoreDirectorFactory, inheritedConfig.getAssertionScoreDirectorFactory());
        return this;
    }

    @Override
    public ScoreDirectorFactoryConfig copyConfig() {
        return new ScoreDirectorFactoryConfig().inherit(this);
    }

}
