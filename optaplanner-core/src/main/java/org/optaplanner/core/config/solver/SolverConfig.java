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

package org.optaplanner.core.config.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.optaplanner.core.config.phase.SolverPhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.termination.TerminationConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.bestsolution.BestSolutionRecaller;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.phase.AbstractSolverPhase;
import org.optaplanner.core.impl.phase.SolverPhase;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.BasicPlumbingTermination;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.termination.Termination;

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
    protected ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = null;

    @XStreamAlias("termination")
    private TerminationConfig terminationConfig;

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
        EnvironmentMode environmentMode = this.environmentMode == null ? EnvironmentMode.REPRODUCIBLE
                : this.environmentMode;
        if (randomSeed != null) {
            solver.setRandomSeed(randomSeed);
        } else {
            if (environmentMode != EnvironmentMode.PRODUCTION) {
                solver.setRandomSeed(DEFAULT_RANDOM_SEED);
            }
        }
        SolutionDescriptor solutionDescriptor = buildSolutionDescriptor();
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig_
                = scoreDirectorFactoryConfig == null ? new ScoreDirectorFactoryConfig()
                : scoreDirectorFactoryConfig;
        ScoreDirectorFactory scoreDirectorFactory = scoreDirectorFactoryConfig_.buildScoreDirectorFactory(
                environmentMode, solutionDescriptor);
        solver.setScoreDirectorFactory(scoreDirectorFactory);
        ScoreDefinition scoreDefinition = scoreDirectorFactory.getScoreDefinition();
        TerminationConfig terminationConfig_ = terminationConfig == null ? new TerminationConfig()
                : terminationConfig;
        Termination termination = terminationConfig_.buildTermination(scoreDefinition, basicPlumbingTermination);
        solver.setTermination(termination);
        BestSolutionRecaller bestSolutionRecaller = buildBestSolutionRecaller(environmentMode);
        solver.setBestSolutionRecaller(bestSolutionRecaller);
        if (CollectionUtils.isEmpty(solverPhaseConfigList)) {
            throw new IllegalArgumentException(
                    "Configure at least 1 phase (for example <localSearch>) in the solver configuration.");
        }
        List<SolverPhase> solverPhaseList = new ArrayList<SolverPhase>(solverPhaseConfigList.size());
        int phaseIndex = 0;
        for (SolverPhaseConfig solverPhaseConfig : solverPhaseConfigList) {
            SolverPhase solverPhase = solverPhaseConfig.buildSolverPhase(phaseIndex, environmentMode,
                    solutionDescriptor, scoreDefinition, termination);
            ((AbstractSolverPhase) solverPhase).setBestSolutionRecaller(bestSolutionRecaller);
            solverPhaseList.add(solverPhase);
            phaseIndex++;
        }
        solver.setSolverPhaseList(solverPhaseList);
        return solver;
    }

    protected BestSolutionRecaller buildBestSolutionRecaller(EnvironmentMode environmentMode) {
        BestSolutionRecaller bestSolutionRecaller = new BestSolutionRecaller();
        if (environmentMode == EnvironmentMode.FULL_ASSERT) {
            bestSolutionRecaller.setAssertBestScoreIsUnmodified(true);
        }
        return bestSolutionRecaller;
    }

    protected SolutionDescriptor buildSolutionDescriptor() {
        if (solutionClass == null) {
            throw new IllegalArgumentException("Configure a <solutionClass> in the solver configuration.");
        }
        SolutionDescriptor solutionDescriptor = new SolutionDescriptor(solutionClass);
        solutionDescriptor.processAnnotations();
        if (CollectionUtils.isEmpty(planningEntityClassSet)) {
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
