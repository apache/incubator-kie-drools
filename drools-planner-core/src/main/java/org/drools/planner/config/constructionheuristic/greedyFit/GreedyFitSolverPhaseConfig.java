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

package org.drools.planner.config.constructionheuristic.greedyFit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.phase.SolverPhaseConfig;
import org.drools.planner.config.util.ConfigUtils;
import org.drools.planner.core.constructionheuristic.greedyFit.DefaultGreedyFitSolverPhase;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitSolverPhase;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.ConstructionHeuristicPickEarlyType;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.DefaultGreedyDecider;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.GreedyDecider;
import org.drools.planner.core.constructionheuristic.greedyFit.selector.GreedyPlanningEntitySelector;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.entity.PlanningEntitySelector;
import org.drools.planner.core.heuristic.selector.variable.PlanningValueSelector;
import org.drools.planner.core.heuristic.selector.variable.PlanningValueWalker;
import org.drools.planner.core.heuristic.selector.variable.PlanningVariableWalker;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.termination.Termination;

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

    public GreedyFitSolverPhase buildSolverPhase(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            ScoreDefinition scoreDefinition, Termination solverTermination) {
        DefaultGreedyFitSolverPhase greedySolverPhase = new DefaultGreedyFitSolverPhase();
        configureSolverPhase(greedySolverPhase, environmentMode, scoreDefinition, solverTermination);
        greedySolverPhase.setGreedyPlanningEntitySelector(buildGreedyPlanningEntitySelector(solutionDescriptor));
        greedySolverPhase.setGreedyDecider(buildGreedyDecider(solutionDescriptor, environmentMode));
        if (environmentMode == EnvironmentMode.DEBUG || environmentMode == EnvironmentMode.TRACE) {
            greedySolverPhase.setAssertStepScoreIsUncorrupted(true);
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
        ConstructionHeuristicPickEarlyType constructionHeuristicPickEarlyType = (this.constructionHeuristicPickEarlyType == null)
                ? ConstructionHeuristicPickEarlyType.NEVER : this.constructionHeuristicPickEarlyType;

        Set<Class<?>> planningEntityClassSet = solutionDescriptor.getPlanningEntityClassSet();
        if (planningEntityClassSet.size() != 1) {
            // TODO Multiple MUST BE SUPPORTED TOO
            throw new UnsupportedOperationException("Currently the greedyFit implementation only supports " +
                    "1 planningEntityClass.");
        }
        Class<?> planningEntityClass = planningEntityClassSet.iterator().next();
        PlanningEntityDescriptor planningEntityDescriptor = solutionDescriptor.getPlanningEntityDescriptor(planningEntityClass);
        PlanningVariableWalker planningVariableWalker = new PlanningVariableWalker(planningEntityDescriptor);
        List<PlanningValueWalker> planningValueWalkerList = new ArrayList<PlanningValueWalker>();
        for (PlanningVariableDescriptor planningVariableDescriptor
                : planningEntityDescriptor.getPlanningVariableDescriptors()) {
            PlanningValueSelector planningValueSelector = new PlanningValueSelector(planningVariableDescriptor);
            // TODO should be configured to do BEST etc.
            PlanningValueWalker planningValueWalker = new PlanningValueWalker(
                    planningVariableDescriptor, planningValueSelector);
            planningValueWalkerList.add(planningValueWalker);
        }
        planningVariableWalker.setPlanningValueWalkerList(planningValueWalkerList);
        greedyDecider.setPlanningVariableWalker(planningVariableWalker);
        
        // TODO greedyDecider.setConstructionHeuristicPickEarlyType(constructionHeuristicPickEarlyType);
        if (environmentMode == EnvironmentMode.TRACE) {
            greedyDecider.setAssertMoveScoreIsUncorrupted(true);
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
