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

package org.drools.planner.config.solver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.io.IOUtils;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.phase.SolverPhaseConfig;
import org.drools.planner.config.score.director.ScoreDirectorFactoryConfig;
import org.drools.planner.config.termination.TerminationConfig;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.Solver;
import org.drools.planner.core.bestsolution.BestSolutionRecaller;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.phase.AbstractSolverPhase;
import org.drools.planner.core.phase.SolverPhase;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.score.director.ScoreDirectorFactory;
import org.drools.planner.core.score.director.drools.DroolsScoreDirectorFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.BasicPlumbingTermination;
import org.drools.planner.core.solver.DefaultSolver;
import org.drools.planner.core.termination.Termination;

@XStreamAlias("solver")
public class SolverConfig {

    protected static final long DEFAULT_RANDOM_SEED = 0L;

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected EnvironmentMode environmentMode = null;
    protected Long randomSeed = null;

    protected Class<? extends Solution> solutionClass = null;
    @XStreamImplicit(itemFieldName = "planningEntityClass")
    protected Set<Class<?>> planningEntityClassSet = null;

    @XStreamAlias("scoreDirectorFactory")
    protected ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();

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

    public Class<? extends Solution> getSolutionClass() {
        return solutionClass;
    }

    public void setSolutionClass(Class<? extends Solution> solutionClass) {
        this.solutionClass = solutionClass;
    }

    public Set<Class<?>> getPlanningEntityClassSet() {
        return planningEntityClassSet;
    }

    public void setPlanningEntityClassSet(Set<Class<?>> planningEntityClassSet) {
        this.planningEntityClassSet = planningEntityClassSet;
    }

    public ScoreDirectorFactoryConfig getScoreDirectorFactoryConfig() {
        return scoreDirectorFactoryConfig;
    }

    public void setScoreDirectorFactoryConfig(ScoreDirectorFactoryConfig scoreDirectorFactoryConfig) {
        this.scoreDirectorFactoryConfig = scoreDirectorFactoryConfig;
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
        BasicPlumbingTermination basicPlumbingTermination = new BasicPlumbingTermination();
        solver.setBasicPlumbingTermination(basicPlumbingTermination);
        if (environmentMode != EnvironmentMode.PRODUCTION) {
            if (randomSeed != null) {
                solver.setRandomSeed(randomSeed);
            } else {
                solver.setRandomSeed(DEFAULT_RANDOM_SEED);
            }
        }
        SolutionDescriptor solutionDescriptor = buildSolutionDescriptor();
        ScoreDirectorFactory scoreDirectorFactory = scoreDirectorFactoryConfig.buildScoreDirectorFactory(
                solutionDescriptor);
        solver.setScoreDirectorFactory(scoreDirectorFactory);
        ScoreDefinition scoreDefinition = scoreDirectorFactory.getScoreDefinition();
        Termination termination = terminationConfig.buildTermination(scoreDefinition, basicPlumbingTermination);
        solver.setTermination(termination);
        BestSolutionRecaller bestSolutionRecaller = buildBestSolutionRecaller();
        solver.setBestSolutionRecaller(bestSolutionRecaller);
        if (solverPhaseConfigList == null || solverPhaseConfigList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Configure at least 1 phase (for example <localSearch>) in the solver configuration.");
        }
        List<SolverPhase> solverPhaseList = new ArrayList<SolverPhase>(solverPhaseConfigList.size());
        for (SolverPhaseConfig solverPhaseConfig : solverPhaseConfigList) {
            SolverPhase solverPhase = solverPhaseConfig.buildSolverPhase(environmentMode,
                    solutionDescriptor, scoreDefinition, termination);
            ((AbstractSolverPhase) solverPhase).setBestSolutionRecaller(bestSolutionRecaller);
            solverPhaseList.add(solverPhase);
        }
        solver.setSolverPhaseList(solverPhaseList);
        return solver;
    }

    protected BestSolutionRecaller buildBestSolutionRecaller() {
        BestSolutionRecaller bestSolutionRecaller = new BestSolutionRecaller();
        if (environmentMode == EnvironmentMode.TRACE) {
            bestSolutionRecaller.setAssertBestSolutionIsUnmodified(true);
        }
        return bestSolutionRecaller;
    }

    protected SolutionDescriptor buildSolutionDescriptor() {
        if (solutionClass == null) {
            throw new IllegalArgumentException("Configure a <solutionClass> in the solver configuration.");
        }
        SolutionDescriptor solutionDescriptor = new SolutionDescriptor(solutionClass);
        solutionDescriptor.processAnnotations();
        if (planningEntityClassSet == null || planningEntityClassSet.isEmpty()) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <planningEntityClass> in the solver configuration.");
        }
        for (Class<?> planningEntityClass : planningEntityClassSet) {
            PlanningEntityDescriptor planningEntityDescriptor = new PlanningEntityDescriptor(
                    solutionDescriptor, planningEntityClass);
            solutionDescriptor.addPlanningEntityDescriptor(planningEntityDescriptor);
            planningEntityDescriptor.processAnnotations();
        }
        return solutionDescriptor;
    }

    public void inherit(SolverConfig inheritedConfig) {
        if (environmentMode == null) {
            environmentMode = inheritedConfig.getEnvironmentMode();
        }
        if (randomSeed == null) {
            randomSeed = inheritedConfig.getRandomSeed();
        }
        if (solutionClass == null) {
            solutionClass = inheritedConfig.getSolutionClass();
        }
        if (planningEntityClassSet == null) {
            planningEntityClassSet = inheritedConfig.getPlanningEntityClassSet();
        } else if (inheritedConfig.getPlanningEntityClassSet() != null) {
            planningEntityClassSet.addAll(inheritedConfig.getPlanningEntityClassSet());
        }
        if (scoreDirectorFactoryConfig == null) {
            scoreDirectorFactoryConfig = inheritedConfig.getScoreDirectorFactoryConfig();
        } else if (inheritedConfig.getScoreDirectorFactoryConfig() != null) {
            scoreDirectorFactoryConfig.inherit(inheritedConfig.getScoreDirectorFactoryConfig());
        }
        if (terminationConfig == null) {
            terminationConfig = inheritedConfig.getTerminationConfig();
        } else if (inheritedConfig.getTerminationConfig() != null) {
            terminationConfig.inherit(inheritedConfig.getTerminationConfig());
        }
        solverPhaseConfigList = ConfigUtils.inheritMergeableListProperty(
                solverPhaseConfigList, inheritedConfig.getSolverPhaseConfigList());
    }

}
