/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.util.Drools;
import org.kie.api.KieBase;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.io.jaxb.adapter.JaxbCustomPropertiesAdapter;
import org.optaplanner.core.impl.score.director.drools.KieRuntimeBuilderWrapper;

@XmlType(propOrder = {
        "easyScoreCalculatorClass",
        "easyScoreCalculatorCustomProperties",
        "constraintProviderClass",
        "constraintProviderCustomProperties",
        "constraintStreamImplType",
        "incrementalScoreCalculatorClass",
        "incrementalScoreCalculatorCustomProperties",
        "scoreDrlList",
        "scoreDrlFileList",
        "droolsAlphaNetworkCompilationEnabled",
        "kieBaseConfigurationProperties",
        "initializingScoreTrend",
        "assertionScoreDirectorFactory"
})
public class ScoreDirectorFactoryConfig extends AbstractConfig<ScoreDirectorFactoryConfig> {

    protected Class<? extends EasyScoreCalculator> easyScoreCalculatorClass = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    protected Map<String, String> easyScoreCalculatorCustomProperties = null;

    protected Class<? extends ConstraintProvider> constraintProviderClass = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    protected Map<String, String> constraintProviderCustomProperties = null;
    protected ConstraintStreamImplType constraintStreamImplType;

    protected Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    protected Map<String, String> incrementalScoreCalculatorCustomProperties = null;

    @XmlElement(name = "scoreDrl")
    protected List<String> scoreDrlList = null;
    @XmlElement(name = "scoreDrlFile")
    protected List<File> scoreDrlFileList = null;
    @XmlTransient
    protected Supplier<KieBase> gizmoKieBaseSupplier = null;

    protected Boolean droolsAlphaNetworkCompilationEnabled = null;
    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    protected Map<String, String> kieBaseConfigurationProperties = null;

    // TODO: this should be rather an enum?
    protected String initializingScoreTrend = null;

    @XmlElement(name = "assertionScoreDirectorFactory")
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

    public void
            setIncrementalScoreCalculatorClass(Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass) {
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

    public Boolean getDroolsAlphaNetworkCompilationEnabled() {
        return droolsAlphaNetworkCompilationEnabled;
    }

    /**
     * @deprecated in favor of {@link #getGizmoKieBaseSupplier}
     */
    @Deprecated(forRemoval = true)
    public KieRuntimeBuilderWrapper getGizmoKieRuntimeBuilderWrapper() {
        return (KieRuntimeBuilderWrapper) getGizmoKieBaseSupplier();
    }

    /**
     * @deprecated in favor of {@link #setGizmoKieBaseSupplier}
     */
    @Deprecated(forRemoval = true)
    public void setGizmoKieRuntimeBuilderWrapper(KieRuntimeBuilderWrapper kieRuntimeBuilderWrapper) {
        setGizmoKieBaseSupplier(kieRuntimeBuilderWrapper);
    }

    /**
     * For internal use only, get the generated Gizmo KieBaseSupplier.
     */
    public Supplier<KieBase> getGizmoKieBaseSupplier() {
        return gizmoKieBaseSupplier;
    }

    /**
     * For internal use only, set the generated Gizmo KieBaseSupplier.
     */
    public void setGizmoKieBaseSupplier(Supplier<KieBase> gizmoKieBaseSupplier) {
        this.gizmoKieBaseSupplier = gizmoKieBaseSupplier;
    }

    public void setDroolsAlphaNetworkCompilationEnabled(Boolean droolsAlphaNetworkCompilationEnabled) {
        this.droolsAlphaNetworkCompilationEnabled = droolsAlphaNetworkCompilationEnabled;
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

    public ScoreDirectorFactoryConfig
            withEasyScoreCalculatorClass(Class<? extends EasyScoreCalculator> easyScoreCalculatorClass) {
        this.easyScoreCalculatorClass = easyScoreCalculatorClass;
        return this;
    }

    public ScoreDirectorFactoryConfig
            withEasyScoreCalculatorCustomProperties(Map<String, String> easyScoreCalculatorCustomProperties) {
        this.easyScoreCalculatorCustomProperties = easyScoreCalculatorCustomProperties;
        return this;
    }

    public ScoreDirectorFactoryConfig withConstraintProviderClass(Class<? extends ConstraintProvider> constraintProviderClass) {
        this.constraintProviderClass = constraintProviderClass;
        return this;
    }

    public ScoreDirectorFactoryConfig
            withConstraintProviderCustomProperties(Map<String, String> constraintProviderCustomProperties) {
        this.constraintProviderCustomProperties = constraintProviderCustomProperties;
        return this;
    }

    public ScoreDirectorFactoryConfig withConstraintStreamImplType(ConstraintStreamImplType constraintStreamImplType) {
        this.constraintStreamImplType = constraintStreamImplType;
        return this;
    }

    public ScoreDirectorFactoryConfig
            withIncrementalScoreCalculatorClass(Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass) {
        this.incrementalScoreCalculatorClass = incrementalScoreCalculatorClass;
        return this;
    }

    public ScoreDirectorFactoryConfig
            withIncrementalScoreCalculatorCustomProperties(Map<String, String> incrementalScoreCalculatorCustomProperties) {
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

    /**
     * @deprecated in favor of {@link #withGizmoKieBaseSupplier}
     */
    @Deprecated(forRemoval = true)
    public ScoreDirectorFactoryConfig withGizmoKieRuntimeBuilderWrapper(KieRuntimeBuilderWrapper kieRuntimeBuilderWrapper) {
        setGizmoKieBaseSupplier(kieRuntimeBuilderWrapper);
        return this;
    }

    /**
     * For internal use only, set the generated Gizmo KieBaseSupplier.
     */
    public ScoreDirectorFactoryConfig withGizmoKieBaseSupplier(Supplier<KieBase> kieBaseSupplier) {
        setGizmoKieBaseSupplier(kieBaseSupplier);
        return this;
    }

    public ScoreDirectorFactoryConfig withDroolsAlphaNetworkCompilationEnabled(
            boolean droolsAlphaNetworkCompilationEnabled) {
        this.droolsAlphaNetworkCompilationEnabled = droolsAlphaNetworkCompilationEnabled;
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
        gizmoKieBaseSupplier = ConfigUtils.inheritOverwritableProperty(gizmoKieBaseSupplier,
                inheritedConfig.getGizmoKieBaseSupplier());
        droolsAlphaNetworkCompilationEnabled = ConfigUtils.inheritOverwritableProperty(
                droolsAlphaNetworkCompilationEnabled, inheritedConfig.getDroolsAlphaNetworkCompilationEnabled());
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

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        classVisitor.accept(easyScoreCalculatorClass);
        classVisitor.accept(constraintProviderClass);
        classVisitor.accept(incrementalScoreCalculatorClass);
        if (assertionScoreDirectorFactory != null) {
            assertionScoreDirectorFactory.visitReferencedClasses(classVisitor);
        }
    }

    private boolean isUsingDrools() {
        if (scoreDrlList != null || scoreDrlFileList != null) { // We know we're in DRL.
            return true;
        } else if (constraintProviderClass == null) { // We know we're neither in DRL nor in CS.
            return false;
        }
        return (constraintStreamImplType == null || constraintStreamImplType == ConstraintStreamImplType.DROOLS);
    }

    public boolean isDroolsAlphaNetworkCompilationEnabled() {
        if (!isUsingDrools()) {
            return false;
        }
        boolean ancEnabledValue = Objects.requireNonNullElse(getDroolsAlphaNetworkCompilationEnabled(), true);
        if (ancEnabledValue) { // ANC does not work in native images.
            return !Drools.isNativeImage();
        } else {
            return false;
        }
    }

}
