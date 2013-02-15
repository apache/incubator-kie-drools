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

package org.drools.planner.config.score.director;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.score.buildin.bendable.BendableScoreDefinition;
import org.drools.planner.core.score.buildin.hardmediumsoft.HardMediumSoftScoreDefinition;
import org.drools.planner.core.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.drools.planner.core.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreDefinition;
import org.drools.planner.core.score.buildin.hardsoftdouble.HardSoftDoubleScoreDefinition;
import org.drools.planner.core.score.buildin.hardsoftlong.HardSoftLongScoreDefinition;
import org.drools.planner.core.score.buildin.simple.SimpleScoreDefinition;
import org.drools.planner.core.score.buildin.simplebigdecimal.SimpleBigDecimalScoreDefinition;
import org.drools.planner.core.score.buildin.simpledouble.SimpleDoubleScoreDefinition;
import org.drools.planner.core.score.buildin.simplelong.SimpleLongScoreDefinition;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.score.director.AbstractScoreDirectorFactory;
import org.drools.planner.core.score.director.ScoreDirectorFactory;
import org.drools.planner.core.score.director.drools.DroolsScoreDirectorFactory;
import org.drools.planner.core.score.director.incremental.IncrementalScoreCalculator;
import org.drools.planner.core.score.director.incremental.IncrementalScoreDirectorFactory;
import org.drools.planner.core.score.director.simple.SimpleScoreCalculator;
import org.drools.planner.core.score.director.simple.SimpleScoreDirectorFactory;

@XStreamAlias("scoreDirectorFactory")
public class ScoreDirectorFactoryConfig {

    protected ScoreDefinition scoreDefinition = null;
    protected Class<? extends ScoreDefinition> scoreDefinitionClass = null;
    protected ScoreDefinitionType scoreDefinitionType = null;
    protected Integer bendableHardScoresSize = null;
    protected Integer bendableSoftScoresSize = null;

    @XStreamOmitField
    protected SimpleScoreCalculator simpleScoreCalculator = null;
    protected Class<? extends SimpleScoreCalculator> simpleScoreCalculatorClass = null;

    protected Class<? extends IncrementalScoreCalculator> incrementalScoreCalculatorClass = null;

    @XStreamOmitField
    protected RuleBase ruleBase = null;
    @XStreamImplicit(itemFieldName = "scoreDrl")
    protected List<String> scoreDrlList = null;

    @XStreamAlias("assertionScoreDirectorFactory")
    protected ScoreDirectorFactoryConfig assertionScoreDirectorFactory = null;

    public ScoreDefinition getScoreDefinition() {
        return scoreDefinition;
    }

    public void setScoreDefinition(ScoreDefinition scoreDefinition) {
        this.scoreDefinition = scoreDefinition;
    }

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

    public Integer getBendableHardScoresSize() {
        return bendableHardScoresSize;
    }

    public void setBendableHardScoresSize(Integer bendableHardScoresSize) {
        this.bendableHardScoresSize = bendableHardScoresSize;
    }

    public Integer getBendableSoftScoresSize() {
        return bendableSoftScoresSize;
    }

    public void setBendableSoftScoresSize(Integer bendableSoftScoresSize) {
        this.bendableSoftScoresSize = bendableSoftScoresSize;
    }

    public SimpleScoreCalculator getSimpleScoreCalculator() {
        return simpleScoreCalculator;
    }

    public void setSimpleScoreCalculator(SimpleScoreCalculator simpleScoreCalculator) {
        this.simpleScoreCalculator = simpleScoreCalculator;
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

    public RuleBase getRuleBase() {
        return ruleBase;
    }

    public void setRuleBase(RuleBase ruleBase) {
        this.ruleBase = ruleBase;
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
            if (assertionScoreDirectorFactory.getScoreDefinition() != null
                    || assertionScoreDirectorFactory.getScoreDefinitionClass() != null
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
        if ((bendableHardScoresSize != null || bendableSoftScoresSize != null)
                && scoreDefinitionType != ScoreDefinitionType.BENDABLE) {
            throw new IllegalArgumentException("With scoreDefinitionType (" + scoreDefinitionType
                    + ") there must be no bendableHardScoresSize (" + bendableHardScoresSize
                    + ") or bendableSoftScoresSize (" + bendableSoftScoresSize + ").");
        }
        if (scoreDefinition != null) {
            return scoreDefinition;
        } else if (scoreDefinitionClass != null) {
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
                    if (bendableHardScoresSize == null || bendableSoftScoresSize == null) {
                        throw new IllegalArgumentException("With scoreDefinitionType (" + scoreDefinitionType
                                + ") there must be a bendableHardScoresSize (" + bendableHardScoresSize
                                + ") and a bendableSoftScoresSize (" + bendableSoftScoresSize + ").");
                    }
                    return new BendableScoreDefinition(bendableHardScoresSize, bendableSoftScoresSize);
                default:
                    throw new IllegalStateException("The scoreDefinitionType (" + scoreDefinitionType
                            + ") is not implemented.");
            }
        } else {
            return new SimpleScoreDefinition();
        }
    }

    private AbstractScoreDirectorFactory buildSimpleScoreDirectorFactory() {
        if (simpleScoreCalculator != null) {
            return new SimpleScoreDirectorFactory(simpleScoreCalculator);
        } else if (simpleScoreCalculatorClass != null) {
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
        scoreDirectorFactory.setRuleBase(buildRuleBase());
        return scoreDirectorFactory;
    }

    private RuleBase buildRuleBase() {
        if (ruleBase != null) {
            if (!CollectionUtils.isEmpty(scoreDrlList)) {
                throw new IllegalArgumentException("If ruleBase is not null, the scoreDrlList (" + scoreDrlList
                        + ") must be empty.");
            }
            return ruleBase;
        } else {
            PackageBuilder packageBuilder = new PackageBuilder();
            for (String scoreDrl : scoreDrlList) {
                InputStream scoreDrlIn = getClass().getResourceAsStream(scoreDrl);
                if (scoreDrlIn == null) {
                    throw new IllegalArgumentException("The scoreDrl (" + scoreDrl
                            + ") does not exist as a classpath resource.");
                }
                try {
                    packageBuilder.addPackageFromDrl(new InputStreamReader(scoreDrlIn, "UTF-8"));
                } catch (DroolsParserException e) {
                    throw new IllegalArgumentException("The scoreDrl (" + scoreDrl + ") could not be loaded.", e);
                } catch (IOException e) {
                    throw new IllegalArgumentException("The scoreDrl (" + scoreDrl + ") could not be loaded.", e);
                } finally {
                    IOUtils.closeQuietly(scoreDrlIn);
                }
            }
            RuleBaseConfiguration ruleBaseConfiguration = new RuleBaseConfiguration();
            RuleBase ruleBase = RuleBaseFactory.newRuleBase(ruleBaseConfiguration);
            if (packageBuilder.hasErrors()) {
                throw new IllegalStateException("There are errors in the scoreDrl's:\n"
                        + packageBuilder.getErrors().toString());
            }
            ruleBase.addPackage(packageBuilder.getPackage());
            return ruleBase;
        }
    }

    public void inherit(ScoreDirectorFactoryConfig inheritedConfig) {
        if (scoreDefinition == null && scoreDefinitionClass == null && scoreDefinitionType == null
                && bendableHardScoresSize == null && bendableSoftScoresSize == null) {
            scoreDefinition = inheritedConfig.getScoreDefinition();
            scoreDefinitionClass = inheritedConfig.getScoreDefinitionClass();
            scoreDefinitionType = inheritedConfig.getScoreDefinitionType();
            bendableHardScoresSize = inheritedConfig.getBendableHardScoresSize();
            bendableSoftScoresSize = inheritedConfig.getBendableSoftScoresSize();
        }
        if (simpleScoreCalculator == null) {
            simpleScoreCalculator = inheritedConfig.getSimpleScoreCalculator();
        }
        if (simpleScoreCalculatorClass == null) {
            simpleScoreCalculatorClass = inheritedConfig.getSimpleScoreCalculatorClass();
        }
        if (incrementalScoreCalculatorClass == null) {
            incrementalScoreCalculatorClass = inheritedConfig.getIncrementalScoreCalculatorClass();
        }
        if (ruleBase == null) {
            ruleBase = inheritedConfig.getRuleBase();
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
