/*
 * Copyright 2010 JBoss Inc
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.PackageBuilder;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardmediumsoft.HardMediumSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoftdouble.HardSoftDoubleScoreDefinition;
import org.optaplanner.core.impl.score.buildin.hardsoftlong.HardSoftLongScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simplebigdecimal.SimpleBigDecimalScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simpledouble.SimpleDoubleScoreDefinition;
import org.optaplanner.core.impl.score.buildin.simplelong.SimpleLongScoreDefinition;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreCalculator;
import org.optaplanner.core.impl.score.director.incremental.IncrementalScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.simple.SimpleScoreCalculator;
import org.optaplanner.core.impl.score.director.simple.SimpleScoreDirectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XStreamAlias("scoreDirectorFactory")
public class ScoreDirectorFactoryConfig {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected Class<? extends ScoreDefinition> scoreDefinitionClass = null;
    protected ScoreDefinitionType scoreDefinitionType = null;
    protected Integer bendableHardLevelCount = null;
    protected Integer bendableSoftLevelCount = null;

    protected Class<? extends SimpleScoreCalculator> simpleScoreCalculatorClass = null;

    protected Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass = null;

    @XStreamOmitField
    protected KieBase kieBase = null;
    @XStreamImplicit(itemFieldName = "scoreDrl")
    protected List<String> scoreDrlList = null;

    @XStreamAlias("assertionScoreDirectorFactory")
    protected ScoreDirectorFactoryConfig assertionScoreDirectorFactory = null;

    public Class<? extends ScoreDefinition> getScoreDefinitionClass() {
        return scoreDefinitionClass;
    }

    public void setScoreDefinitionClass(Class<? extends ScoreDefinition> scoreDefinitionClass) {
        this.scoreDefinitionClass = scoreDefinitionClass;
    }

    public ScoreDefinitionType getScoreDefinitionType() {
        return scoreDefinitionType;
    }

    public void setScoreDefinitionType(ScoreDefinitionType scoreDefinitionType) {
        this.scoreDefinitionType = scoreDefinitionType;
    }

    public Integer getBendableHardLevelCount() {
        return bendableHardLevelCount;
    }

    public void setBendableHardLevelCount(Integer bendableHardLevelCount) {
        this.bendableHardLevelCount = bendableHardLevelCount;
    }

    public Integer getBendableSoftLevelCount() {
        return bendableSoftLevelCount;
    }

    public void setBendableSoftLevelCount(Integer bendableSoftLevelCount) {
        this.bendableSoftLevelCount = bendableSoftLevelCount;
    }

    public Class<? extends SimpleScoreCalculator> getSimpleScoreCalculatorClass() {
        return simpleScoreCalculatorClass;
    }

    public void setSimpleScoreCalculatorClass(Class<? extends SimpleScoreCalculator> simpleScoreCalculatorClass) {
        this.simpleScoreCalculatorClass = simpleScoreCalculatorClass;
    }

    public Class<? extends IncrementalScoreCalculator> getIncrementalScoreCalculatorClass() {
        return incrementalScoreCalculatorClass;
    }

    public void setIncrementalScoreCalculatorClass(Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass) {
        this.incrementalScoreCalculatorClass = incrementalScoreCalculatorClass;
    }

    public KieBase getKieBase() {
        return kieBase;
    }

    public void setKieBase(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public List<String> getScoreDrlList() {
        return scoreDrlList;
    }

    public void setScoreDrlList(List<String> scoreDrlList) {
        this.scoreDrlList = scoreDrlList;
    }

    public ScoreDirectorFactoryConfig getAssertionScoreDirectorFactory() {
        return assertionScoreDirectorFactory;
    }

    public void setAssertionScoreDirectorFactory(ScoreDirectorFactoryConfig assertionScoreDirectorFactory) {
        this.assertionScoreDirectorFactory = assertionScoreDirectorFactory;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public ScoreDirectorFactory buildScoreDirectorFactory(EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor) {
        ScoreDefinition scoreDefinition = buildScoreDefinition();
        return buildScoreDirectorFactory(environmentMode, solutionDescriptor, scoreDefinition);
    }

    protected ScoreDirectorFactory buildScoreDirectorFactory(EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor, ScoreDefinition scoreDefinition) {
        AbstractScoreDirectorFactory scoreDirectorFactory;
        // TODO this should fail-fast if multiple scoreDirectorFactory's are configured or if non are configured
        scoreDirectorFactory = buildSimpleScoreDirectorFactory();
        if (scoreDirectorFactory == null) {
            scoreDirectorFactory = buildIncrementalScoreDirectorFactory();
        }
        if (scoreDirectorFactory == null) {
            scoreDirectorFactory = buildDroolsScoreDirectorFactory();
        }
        scoreDirectorFactory.setSolutionDescriptor(solutionDescriptor);
        scoreDirectorFactory.setScoreDefinition(scoreDefinition);
        if (assertionScoreDirectorFactory != null) {
            if (assertionScoreDirectorFactory.getAssertionScoreDirectorFactory() != null) {
                throw new IllegalArgumentException("A assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory + ") cannot have a non-null assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory.getAssertionScoreDirectorFactory() + ").");
            }
            if (assertionScoreDirectorFactory.getScoreDefinitionClass() != null
                    || assertionScoreDirectorFactory.getScoreDefinitionType() != null) {
                throw new IllegalArgumentException("A assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory + ") must reuse the scoreDefinition of its parent." +
                        " It cannot have a non-null scoreDefinition* property.");
            }
            if (environmentMode.compareTo(EnvironmentMode.FAST_ASSERT) > 0) {
                throw new IllegalArgumentException("A non-null assertionScoreDirectorFactory ("
                        + assertionScoreDirectorFactory + ") requires an environmentMode ("
                        + environmentMode + ") of " + EnvironmentMode.FAST_ASSERT + " or lower.");
            }
            scoreDirectorFactory.setAssertionScoreDirectorFactory(
                    assertionScoreDirectorFactory.buildScoreDirectorFactory(
                            EnvironmentMode.PRODUCTION, solutionDescriptor, scoreDefinition));
        }
        return scoreDirectorFactory;
    }

    public ScoreDefinition buildScoreDefinition() {
        if ((bendableHardLevelCount != null || bendableSoftLevelCount != null)
                && scoreDefinitionType != ScoreDefinitionType.BENDABLE) {
            throw new IllegalArgumentException("With scoreDefinitionType (" + scoreDefinitionType
                    + ") there must be no bendableHardLevelCount (" + bendableHardLevelCount
                    + ") or bendableSoftLevelCount (" + bendableSoftLevelCount + ").");
        }
        if (scoreDefinitionClass != null) {
            return ConfigUtils.newInstance(this, "scoreDefinitionClass", scoreDefinitionClass);
        } else if (scoreDefinitionType != null) {
            switch (scoreDefinitionType) {
                case SIMPLE:
                    return new SimpleScoreDefinition();
                case SIMPLE_LONG:
                    return new SimpleLongScoreDefinition();
                case SIMPLE_DOUBLE:
                    return new SimpleDoubleScoreDefinition();
                case SIMPLE_BIG_DECIMAL:
                    return new SimpleBigDecimalScoreDefinition();
                case HARD_SOFT:
                    return new HardSoftScoreDefinition();
                case HARD_SOFT_LONG:
                    return new HardSoftLongScoreDefinition();
                case HARD_SOFT_DOUBLE:
                    return new HardSoftDoubleScoreDefinition();
                case HARD_SOFT_BIG_DECIMAL:
                    return new HardSoftBigDecimalScoreDefinition();
                case HARD_MEDIUM_SOFT:
                    return new HardMediumSoftScoreDefinition();
                case BENDABLE:
                    if (bendableHardLevelCount == null || bendableSoftLevelCount == null) {
                        throw new IllegalArgumentException("With scoreDefinitionType (" + scoreDefinitionType
                                + ") there must be a bendableHardLevelCount (" + bendableHardLevelCount
                                + ") and a bendableSoftLevelCount (" + bendableSoftLevelCount + ").");
                    }
                    return new BendableScoreDefinition(bendableHardLevelCount, bendableSoftLevelCount);
                default:
                    throw new IllegalStateException("The scoreDefinitionType (" + scoreDefinitionType
                            + ") is not implemented.");
            }
        } else {
            return new SimpleScoreDefinition();
        }
    }

    private AbstractScoreDirectorFactory buildSimpleScoreDirectorFactory() {
        if (simpleScoreCalculatorClass != null) {
            SimpleScoreCalculator simpleScoreCalculator = ConfigUtils.newInstance(this,
                    "simpleScoreCalculatorClass", simpleScoreCalculatorClass);
            return new SimpleScoreDirectorFactory(simpleScoreCalculator);
        } else {
            return null;
        }
    }

    private AbstractScoreDirectorFactory buildIncrementalScoreDirectorFactory() {
        if (incrementalScoreCalculatorClass != null) {
            return new IncrementalScoreDirectorFactory(incrementalScoreCalculatorClass);
        } else {
            return null;
        }
    }

    private AbstractScoreDirectorFactory buildDroolsScoreDirectorFactory() {
        DroolsScoreDirectorFactory scoreDirectorFactory = new DroolsScoreDirectorFactory();
        scoreDirectorFactory.setKieBase(buildKieBase());
        return scoreDirectorFactory;
    }

    private KieBase buildKieBase() {
        if (kieBase != null) {
            if (!CollectionUtils.isEmpty(scoreDrlList)) {
                throw new IllegalArgumentException("If kieBase is not null, the scoreDrlList (" + scoreDrlList
                        + ") must be empty.");
            }
            return kieBase;
        } else {
            KieServices kieServices = KieServices.Factory.get();
            KieResources kieResources = kieServices.getResources();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            for (String scoreDrl : scoreDrlList) {
                InputStream scoreDrlIn = getClass().getResourceAsStream(scoreDrl);
                if (scoreDrlIn == null) {
                    throw new IllegalArgumentException("The scoreDrl (" + scoreDrl
                            + ") does not exist as a classpath resource.");
                }
                String path = "src/main/resources/optaplanner-kie-namespace/" + scoreDrl;
                kieFileSystem.write(path, kieResources.newInputStreamResource(scoreDrlIn, "UTF-8"));
                // TODO use getResource() and newClassPathResource() instead
                // URL scoreDrlURL = getClass().getResource(scoreDrl);
                // if (scoreDrlURL == null) ...
                // kieFileSystem.write(kieResources.newClassPathResource(scoreDrl, "UTF-8"));
            }
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            Results results = kieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                throw new IllegalStateException("There are errors in the scoreDrl's:\n"
                        + results.toString());
            } else if (results.hasMessages(Message.Level.WARNING)) {
                logger.warn("There are warning in the scoreDrl's:\n"
                        + results.toString());
            }
            KieContainer kieContainer = kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
            KieBase kieBase = kieContainer.getKieBase();
            // ruleBaseConfiguration.setOption(PhreakOption.ENABLED);
            return kieBase;
        }
    }

    public void inherit(ScoreDirectorFactoryConfig inheritedConfig) {
        if (scoreDefinitionClass == null && scoreDefinitionType == null
                && bendableHardLevelCount == null && bendableSoftLevelCount == null) {
            scoreDefinitionClass = inheritedConfig.getScoreDefinitionClass();
            scoreDefinitionType = inheritedConfig.getScoreDefinitionType();
            bendableHardLevelCount = inheritedConfig.getBendableHardLevelCount();
            bendableSoftLevelCount = inheritedConfig.getBendableSoftLevelCount();
        }
        if (simpleScoreCalculatorClass == null) {
            simpleScoreCalculatorClass = inheritedConfig.getSimpleScoreCalculatorClass();
        }
        if (incrementalScoreCalculatorClass == null) {
            incrementalScoreCalculatorClass = inheritedConfig.getIncrementalScoreCalculatorClass();
        }
        if (kieBase == null) {
            kieBase = inheritedConfig.getKieBase();
        }
        scoreDrlList = ConfigUtils.inheritMergeableListProperty(
                scoreDrlList, inheritedConfig.getScoreDrlList());
        if (assertionScoreDirectorFactory == null) {
            assertionScoreDirectorFactory = inheritedConfig.getAssertionScoreDirectorFactory();
        }
    }

    public static enum ScoreDefinitionType {
        SIMPLE,
        SIMPLE_LONG,
        SIMPLE_DOUBLE,
        SIMPLE_BIG_DECIMAL,
        HARD_SOFT,
        HARD_SOFT_LONG,
        HARD_SOFT_DOUBLE,
        HARD_SOFT_BIG_DECIMAL,
        HARD_MEDIUM_SOFT,
        BENDABLE,
    }

}
