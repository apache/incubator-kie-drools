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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.io.IOUtils;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.planner.config.localsearch.termination.TerminationConfig;
import org.drools.planner.config.score.definition.ScoreDefinitionConfig;
import org.drools.planner.core.Solver;
import org.drools.planner.core.bestsolution.BestSolutionRecaller;
import org.drools.planner.core.domain.meta.PlanningEntityDescriptor;
import org.drools.planner.core.domain.meta.SolutionDescriptor;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.AbstractSolverPhase;
import org.drools.planner.core.solver.DefaultSolver;
import org.drools.planner.core.solver.SolverPhase;

@XStreamAlias("solver")
public class SolverConfig {

    protected static final long DEFAULT_RANDOM_SEED = 0L;

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected EnvironmentMode environmentMode = null;
    protected Long randomSeed = null;

    protected Class<Solution> solutionClass = null;
    @XStreamImplicit(itemFieldName = "planningEntityClass")
    protected Set<Class<?>> planningEntityClassSet = null;

    @XStreamOmitField
    protected RuleBase ruleBase = null;
    @XStreamImplicit(itemFieldName = "scoreDrl")
    protected List<String> scoreDrlList = null;
    @XStreamAlias("scoreDefinition")
    protected ScoreDefinitionConfig scoreDefinitionConfig = new ScoreDefinitionConfig();

    @XStreamAlias("termination")
    private TerminationConfig terminationConfig = new TerminationConfig();

    @XStreamImplicit()
    protected List<SolverPhaseConfig> solverPhaseConfigList = null;

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

    public Class<Solution> getSolutionClass() {
        return solutionClass;
    }

    public void setSolutionClass(Class<Solution> solutionClass) {
        this.solutionClass = solutionClass;
    }

    public Set<Class<?>> getPlanningEntityClassSet() {
        return planningEntityClassSet;
    }

    public void setPlanningEntityClassSet(Set<Class<?>> planningEntityClassSet) {
        this.planningEntityClassSet = planningEntityClassSet;
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

    public TerminationConfig getTerminationConfig() {
        return terminationConfig;
    }

    public void setTerminationConfig(TerminationConfig terminationConfig) {
        this.terminationConfig = terminationConfig;
    }

    public List<SolverPhaseConfig> getSolverPhaseConfigList() {
        return solverPhaseConfigList;
    }

    public void setSolverPhaseConfigList(List<SolverPhaseConfig> solverPhaseConfigList) {
        this.solverPhaseConfigList = solverPhaseConfigList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public Solver buildSolver() {
        DefaultSolver solver = new DefaultSolver();
        AtomicBoolean terminatedEarlyHolder = new AtomicBoolean(false);
        solver.setTerminatedEarlyHolder(terminatedEarlyHolder);
        if (environmentMode != EnvironmentMode.PRODUCTION) {
            if (randomSeed != null) {
                solver.setRandomSeed(randomSeed);
            } else {
                solver.setRandomSeed(DEFAULT_RANDOM_SEED);
            }
        }
        solver.setSolutionDescriptor(buildSolutionDescriptor());
        solver.setRuleBase(buildRuleBase());
        ScoreDefinition scoreDefinition = scoreDefinitionConfig.buildScoreDefinition();
        solver.setScoreDefinition(scoreDefinition);
        // remove when score-in-solution is refactored
        solver.setScoreCalculator(scoreDefinitionConfig.buildScoreCalculator());
        BestSolutionRecaller bestSolutionRecaller = new BestSolutionRecaller();
        solver.setBestSolutionRecaller(bestSolutionRecaller);

        // TODO solver.setTermination(terminationConfig.buildTermination(scoreDefinition));

        if (solverPhaseConfigList == null) {
            throw new IllegalArgumentException(
                    "Configure <phases> in the solver configuration.");
        }
        List<SolverPhase> solverPhaseList = new ArrayList<SolverPhase>(solverPhaseConfigList.size());
        for (SolverPhaseConfig solverPhaseConfig : solverPhaseConfigList) {
            SolverPhase solverPhase = solverPhaseConfig.buildSolverPhase(environmentMode, scoreDefinition);
            ((AbstractSolverPhase) solverPhase).setTerminatedEarlyHolder(terminatedEarlyHolder);
            ((AbstractSolverPhase) solverPhase).setBestSolutionRecaller(bestSolutionRecaller);
            solverPhaseList.add(solverPhase);
        }
        solver.setSolverPhaseList(solverPhaseList);
        return solver;
    }

    private SolutionDescriptor buildSolutionDescriptor() {
        if (solutionClass == null) {
            throw new IllegalArgumentException("Configure a <solutionClass> in the solver configuration.");
        }
        SolutionDescriptor solutionDescriptor = new SolutionDescriptor(solutionClass);
        if (planningEntityClassSet == null) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <planningEntityClass> in the solver configuration.");
        }
        for (Class<?> planningEntityClass : planningEntityClassSet) {
            PlanningEntityDescriptor planningEntityDescriptor = new PlanningEntityDescriptor(
                    solutionDescriptor, planningEntityClass);
            solutionDescriptor.addPlanningEntityDescriptor(planningEntityDescriptor);
        }
        return solutionDescriptor;
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

    public void inherit(SolverConfig inheritedConfig) {
        if (environmentMode == null) {
            environmentMode = inheritedConfig.getEnvironmentMode();
        }
        if (randomSeed == null) {
            randomSeed = inheritedConfig.getRandomSeed();
        }
        if (planningEntityClassSet == null) {
            planningEntityClassSet = inheritedConfig.getPlanningEntityClassSet();
        } else if (inheritedConfig.getPlanningEntityClassSet() != null) {
            planningEntityClassSet.addAll(inheritedConfig.getPlanningEntityClassSet());
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
        if (terminationConfig == null) {
            terminationConfig = inheritedConfig.getTerminationConfig();
        } else if (inheritedConfig.getTerminationConfig() != null) {
            terminationConfig.inherit(inheritedConfig.getTerminationConfig());
        }
        if (solverPhaseConfigList == null) {
            solverPhaseConfigList = inheritedConfig.getSolverPhaseConfigList();
        } else if (inheritedConfig.getSolverPhaseConfigList() != null) {
            // The inherited solverPhaseConfigList should be before the non-inherited solverPhaseConfigList.
            List<SolverPhaseConfig> mergedList
                    = new ArrayList<SolverPhaseConfig>(inheritedConfig.getSolverPhaseConfigList());
            mergedList.addAll(solverPhaseConfigList);
            solverPhaseConfigList = mergedList;
        }
    }

}
