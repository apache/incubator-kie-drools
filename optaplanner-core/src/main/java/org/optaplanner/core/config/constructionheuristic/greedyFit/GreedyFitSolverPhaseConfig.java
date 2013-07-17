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

package org.optaplanner.core.config.constructionheuristic.greedyFit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.phase.SolverPhaseConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.DefaultGreedyFitSolverPhase;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.GreedyFitSolverPhase;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.ConstructionHeuristicPickEarlyType;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.decider.DefaultGreedyDecider;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.decider.GreedyDecider;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.selector.GreedyPlanningEntitySelector;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.PlanningEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningValueSelector;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningValueWalker;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningVariableWalker;
import org.optaplanner.core.impl.termination.Termination;

@XStreamAlias("greedyFit")
@Deprecated // Use ConstructionHeuristicSolverPhaseConfig
public class GreedyFitSolverPhaseConfig extends SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XStreamImplicit(itemFieldName = "greedyFitPlanningEntity")
    protected List<GreedyFitPlanningEntityConfig> greedyFitPlanningEntityConfigList = null;

    protected ConstructionHeuristicPickEarlyType constructionHeuristicPickEarlyType = null;

    public List<GreedyFitPlanningEntityConfig> getGreedyFitPlanningEntityConfigList() {
        return greedyFitPlanningEntityConfigList;
    }

    public void setGreedyFitPlanningEntityConfigList(List<GreedyFitPlanningEntityConfig> greedyFitPlanningEntityConfigList) {
        this.greedyFitPlanningEntityConfigList = greedyFitPlanningEntityConfigList;
    }

    public ConstructionHeuristicPickEarlyType getConstructionHeuristicPickEarlyType() {
        return constructionHeuristicPickEarlyType;
    }

    public void setConstructionHeuristicPickEarlyType(ConstructionHeuristicPickEarlyType constructionHeuristicPickEarlyType) {
        this.constructionHeuristicPickEarlyType = constructionHeuristicPickEarlyType;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public GreedyFitSolverPhase buildSolverPhase(int phaseIndex, HeuristicConfigPolicy solverConfigPolicy,
            Termination solverTermination) {
        HeuristicConfigPolicy phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        phaseConfigPolicy.setInitializedChainedValueFilterEnabled(true);
        DefaultGreedyFitSolverPhase greedySolverPhase = new DefaultGreedyFitSolverPhase();
        configureSolverPhase(greedySolverPhase, phaseIndex, phaseConfigPolicy, solverTermination);
        greedySolverPhase.setGreedyPlanningEntitySelector(buildGreedyPlanningEntitySelector(
                phaseConfigPolicy.getSolutionDescriptor()));
        greedySolverPhase.setGreedyDecider(buildGreedyDecider(phaseConfigPolicy.getSolutionDescriptor(),
                phaseConfigPolicy.getEnvironmentMode()));
        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            greedySolverPhase.setAssertStepScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            greedySolverPhase.setAssertExpectedStepScore(true);
        }
        return greedySolverPhase;
    }

    private GreedyPlanningEntitySelector buildGreedyPlanningEntitySelector(SolutionDescriptor solutionDescriptor) {
        GreedyPlanningEntitySelector greedyPlanningEntitySelector = new GreedyPlanningEntitySelector();
        List<PlanningEntitySelector> planningEntitySelectorList = new ArrayList<PlanningEntitySelector>(greedyFitPlanningEntityConfigList.size());
        for (GreedyFitPlanningEntityConfig greedyFitPlanningEntityConfig : greedyFitPlanningEntityConfigList) {
            planningEntitySelectorList.add(greedyFitPlanningEntityConfig.buildPlanningEntitySelector(solutionDescriptor));
        }
        greedyPlanningEntitySelector.setPlanningEntitySelectorList(planningEntitySelectorList);
        return greedyPlanningEntitySelector;
    }

    private GreedyDecider buildGreedyDecider(SolutionDescriptor solutionDescriptor, EnvironmentMode environmentMode) {
        DefaultGreedyDecider greedyDecider = new DefaultGreedyDecider();
        ConstructionHeuristicPickEarlyType pickEarlyType = (this.constructionHeuristicPickEarlyType == null)
                ? ConstructionHeuristicPickEarlyType.NEVER : this.constructionHeuristicPickEarlyType;

        Collection<PlanningEntityDescriptor> entityDescriptors = solutionDescriptor.getGenuineEntityDescriptors();
        if (entityDescriptors.size() != 1) {
            // TODO Multiple MUST BE SUPPORTED TOO
            throw new UnsupportedOperationException("Currently the greedyFit implementation only supports " +
                    "1 planningEntityClass.");
        }
        PlanningEntityDescriptor entityDescriptor = entityDescriptors.iterator().next();
        PlanningVariableWalker planningVariableWalker = new PlanningVariableWalker(entityDescriptor);
        List<PlanningValueWalker> planningValueWalkerList = new ArrayList<PlanningValueWalker>();
        for (PlanningVariableDescriptor variableDescriptor
                : entityDescriptor.getVariableDescriptors()) {
            PlanningValueSelector planningValueSelector = new PlanningValueSelector(variableDescriptor);
            // TODO should be configured to do BEST etc.
            PlanningValueWalker planningValueWalker = new PlanningValueWalker(
                    variableDescriptor, planningValueSelector);
            planningValueWalkerList.add(planningValueWalker);
        }
        planningVariableWalker.setPlanningValueWalkerList(planningValueWalkerList);
        greedyDecider.setPlanningVariableWalker(planningVariableWalker);
        
        // TODO greedyDecider.setConstructionHeuristicPickEarlyType(pickEarlyType);
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            greedyDecider.setAssertMoveScoreFromScratch(true);
        }
        return greedyDecider;
    }

    public void inherit(GreedyFitSolverPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        greedyFitPlanningEntityConfigList = ConfigUtils.inheritMergeableListProperty(
                greedyFitPlanningEntityConfigList, inheritedConfig.getGreedyFitPlanningEntityConfigList());
        if (constructionHeuristicPickEarlyType == null) {
            constructionHeuristicPickEarlyType = inheritedConfig.getConstructionHeuristicPickEarlyType();
        }
    }

}
