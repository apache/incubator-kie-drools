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

import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.io.jaxb.adapter.JaxbCustomPropertiesAdapter;

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

    @Deprecated(forRemoval = true)
    @XmlElement(name = "scoreDrl")
    protected List<String> scoreDrlList = null;
    @Deprecated(forRemoval = true)
    @XmlElement(name = "scoreDrlFile")
    protected List<File> scoreDrlFileList = null;
    @XmlTransient
    protected Supplier gizmoKieBaseSupplier = null;

    protected Boolean droolsAlphaNetworkCompilationEnabled = null;
    @Deprecated(forRemoval = true)
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

    /**
     * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
     *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
     *             Streams migration recipe</a>.
     */
    @Deprecated(forRemoval = true)
    public List<String> getScoreDrlList() {
        return scoreDrlList;
    }

    /**
     * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
     *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
     *             Streams migration recipe</a>.
     */
    @Deprecated(forRemoval = true)
    public void setScoreDrlList(List<String> scoreDrlList) {
        this.scoreDrlList = scoreDrlList;
    }

    /**
     * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
     *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
     *             Streams migration recipe</a>.
     */
    @Deprecated(forRemoval = true)
    public List<File> getScoreDrlFileList() {
        return scoreDrlFileList;
    }

    /**
     * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
     *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
     *             Streams migration recipe</a>.
     */
    @Deprecated(forRemoval = true)
    public void setScoreDrlFileList(List<File> scoreDrlFileList) {
        this.scoreDrlFileList = scoreDrlFileList;
    }

    public Boolean getDroolsAlphaNetworkCompilationEnabled() {
        return droolsAlphaNetworkCompilationEnabled;
    }

    /**
     * For internal use only, get the generated Gizmo KieBaseSupplier.
     */
    public Supplier getGizmoKieBaseSupplier() {
        return gizmoKieBaseSupplier;
    }

    /**
     * For internal use only, set the generated Gizmo KieBaseSupplier.
     */
    public void setGizmoKieBaseSupplier(Supplier gizmoKieBaseSupplier) {
        this.gizmoKieBaseSupplier = gizmoKieBaseSupplier;
    }

    public void setDroolsAlphaNetworkCompilationEnabled(Boolean droolsAlphaNetworkCompilationEnabled) {
        this.droolsAlphaNetworkCompilationEnabled = droolsAlphaNetworkCompilationEnabled;
    }

    /**
     * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
     *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
     *             Streams migration recipe</a>.
     */
    @Deprecated(forRemoval = true)
    public Map<String, String> getKieBaseConfigurationProperties() {
        return kieBaseConfigurationProperties;
    }

    /**
     * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
     *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
     *             Streams migration recipe</a>.
     */
    @Deprecated(forRemoval = true)
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

    /**
     * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
     *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
     *             Streams migration recipe</a>.
     */
    @Deprecated(forRemoval = true)
    public ScoreDirectorFactoryConfig withScoreDrlList(List<String> scoreDrlList) {
        this.scoreDrlList = scoreDrlList;
        return this;
    }

    /**
     * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
     *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
     *             Streams migration recipe</a>.
     */
    @Deprecated(forRemoval = true)
    public ScoreDirectorFactoryConfig withScoreDrls(String... scoreDrls) {
        this.scoreDrlList = Arrays.asList(scoreDrls);
        return this;
    }

    /**
     * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
     *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
     *             Streams migration recipe</a>.
     */
    @Deprecated(forRemoval = true)
    public ScoreDirectorFactoryConfig withScoreDrlFileList(List<File> scoreDrlFileList) {
        this.scoreDrlFileList = scoreDrlFileList;
        return this;
    }

    /**
     * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
     *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
     *             Streams migration recipe</a>.
     */
    @Deprecated(forRemoval = true)
    public ScoreDirectorFactoryConfig withScoreDrlFiles(File... scoreDrlFiles) {
        this.scoreDrlFileList = Arrays.asList(scoreDrlFiles);
        return this;
    }

    /**
     * For internal use only, set the generated Gizmo KieBaseSupplier.
     */
    public ScoreDirectorFactoryConfig withGizmoKieBaseSupplier(Supplier kieBaseSupplier) {
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
            return !ConfigUtils.isNativeImage();
        } else {
            return false;
        }
    }

}
