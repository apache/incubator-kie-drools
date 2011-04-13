/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.io.IOUtils;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.planner.config.score.definition.ScoreDefinitionConfig;
import org.drools.planner.core.Solver;
import org.drools.planner.core.bestsolution.BestSolutionRecaller;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.initializer.StartingSolutionInitializer;
import org.drools.planner.core.solver.AbstractSolver;

public abstract class AbstractSolverConfig {

    protected static final long DEFAULT_RANDOM_SEED = 0L;

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected EnvironmentMode environmentMode = null;
    protected Long randomSeed = null;

    @XStreamOmitField
    protected RuleBase ruleBase = null;
    @XStreamImplicit(itemFieldName = "scoreDrl")
    protected List<String> scoreDrlList = null;
    @XStreamAlias("scoreDefinition")
    protected ScoreDefinitionConfig scoreDefinitionConfig = new ScoreDefinitionConfig();

    protected StartingSolutionInitializer startingSolutionInitializer = null; // TODO must be @XStreamOmitField too?
    protected Class<StartingSolutionInitializer> startingSolutionInitializerClass = null;

    public EnvironmentMode getEnvironmentMode() {
        return environmentMode;
    }

    public void setEnvironmentMode(EnvironmentMode environmentMode) {
        this.environmentMode = environmentMode;
    }

    public Long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(Long randomSeed) {
        this.randomSeed = randomSeed;
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

    public ScoreDefinitionConfig getScoreDefinitionConfig() {
        return scoreDefinitionConfig;
    }

    public void setScoreDefinitionConfig(ScoreDefinitionConfig scoreDefinitionConfig) {
        this.scoreDefinitionConfig = scoreDefinitionConfig;
    }

    public StartingSolutionInitializer getStartingSolutionInitializer() {
        return startingSolutionInitializer;
    }

    public void setStartingSolutionInitializer(StartingSolutionInitializer startingSolutionInitializer) {
        this.startingSolutionInitializer = startingSolutionInitializer;
    }

    public Class<StartingSolutionInitializer> getStartingSolutionInitializerClass() {
        return startingSolutionInitializerClass;
    }

    public void setStartingSolutionInitializerClass(Class<StartingSolutionInitializer> startingSolutionInitializerClass) {
        this.startingSolutionInitializerClass = startingSolutionInitializerClass;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public abstract Solver buildSolver();

    protected ScoreDefinition configureAbstractSolver(AbstractSolver abstractSolver) {
        if (environmentMode != EnvironmentMode.PRODUCTION) {
            if (randomSeed != null) {
                abstractSolver.setRandomSeed(randomSeed);
            } else {
                abstractSolver.setRandomSeed(DEFAULT_RANDOM_SEED);
            }
        }
        abstractSolver.setRuleBase(buildRuleBase());
        ScoreDefinition scoreDefinition = scoreDefinitionConfig.buildScoreDefinition();
        abstractSolver.setScoreDefinition(scoreDefinition);
        // remove when score-in-solution is refactored
        abstractSolver.setScoreCalculator(scoreDefinitionConfig.buildScoreCalculator());
        abstractSolver.setStartingSolutionInitializer(buildStartingSolutionInitializer());
        abstractSolver.setBestSolutionRecaller(new BestSolutionRecaller());
        return scoreDefinition;
    }

    private RuleBase buildRuleBase() {
        if (ruleBase != null) {
            if (scoreDrlList != null && !scoreDrlList.isEmpty()) {
                throw new IllegalArgumentException("If ruleBase is not null, the scoreDrlList (" + scoreDrlList
                        + ") must be empty.");
            }
            return ruleBase;
        } else {
            PackageBuilder packageBuilder = new PackageBuilder();
            for (String scoreDrl : scoreDrlList) {
                InputStream scoreDrlIn = getClass().getResourceAsStream(scoreDrl);
                if (scoreDrlIn == null) {
                    throw new IllegalArgumentException("scoreDrl (" + scoreDrl + ") does not exist as a classpath resource.");
                }
                try {
                    packageBuilder.addPackageFromDrl(new InputStreamReader(scoreDrlIn, "utf-8"));
                } catch (DroolsParserException e) {
                    throw new IllegalArgumentException("scoreDrl (" + scoreDrl + ") could not be loaded.", e);
                } catch (IOException e) {
                    throw new IllegalArgumentException("scoreDrl (" + scoreDrl + ") could not be loaded.", e);
                } finally {
                    IOUtils.closeQuietly(scoreDrlIn);
                }
            }
            RuleBaseConfiguration ruleBaseConfiguration = new RuleBaseConfiguration();
            RuleBase ruleBase = RuleBaseFactory.newRuleBase(ruleBaseConfiguration);
            if (packageBuilder.hasErrors()) {
                throw new IllegalStateException("There are errors in the scoreDrl's:"
                        + packageBuilder.getErrors().toString());
            }
            ruleBase.addPackage(packageBuilder.getPackage());
            return ruleBase;
        }
    }

    public StartingSolutionInitializer buildStartingSolutionInitializer() {
        if (startingSolutionInitializer != null) {
            return startingSolutionInitializer;
        } else if (startingSolutionInitializerClass != null) {
            try {
                return startingSolutionInitializerClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("startingSolutionInitializerClass ("
                        + startingSolutionInitializerClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("startingSolutionInitializerClass ("
                        + startingSolutionInitializerClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
        } else {
            return null;
        }
    }

    public void inherit(AbstractSolverConfig inheritedConfig) {
        if (environmentMode == null) {
            environmentMode = inheritedConfig.getEnvironmentMode();
        }
        if (randomSeed == null) {
            randomSeed = inheritedConfig.getRandomSeed();
        }
        if (scoreDrlList == null) {
            scoreDrlList = inheritedConfig.getScoreDrlList();
        } else {
            List<String> inheritedScoreDrlList = inheritedConfig.getScoreDrlList();
            if (inheritedScoreDrlList != null) {
                for (String inheritedScoreDrl : inheritedScoreDrlList) {
                    if (!scoreDrlList.contains(inheritedScoreDrl)) {
                        scoreDrlList.add(inheritedScoreDrl);
                    }
                }
            }
        }
        if (scoreDefinitionConfig == null) {
            scoreDefinitionConfig = inheritedConfig.getScoreDefinitionConfig();
        } else if (inheritedConfig.getScoreDefinitionConfig() != null) {
            scoreDefinitionConfig.inherit(inheritedConfig.getScoreDefinitionConfig());
        }
        if (startingSolutionInitializer == null && startingSolutionInitializerClass == null) {
            startingSolutionInitializer = inheritedConfig.getStartingSolutionInitializer();
            startingSolutionInitializerClass = inheritedConfig.getStartingSolutionInitializerClass();
        }
    }

}
