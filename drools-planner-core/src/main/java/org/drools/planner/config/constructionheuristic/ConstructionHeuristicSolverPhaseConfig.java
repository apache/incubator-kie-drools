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

package org.drools.planner.config.constructionheuristic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.constructionheuristic.placer.entity.EntityPlacerConfig;
import org.drools.planner.config.phase.SolverPhaseConfig;
import org.drools.planner.core.constructionheuristic.DefaultConstructionHeuristicSolverPhase;
import org.drools.planner.core.constructionheuristic.greedyFit.DefaultGreedyFitSolverPhase;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitSolverPhase;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.ConstructionHeuristicPickEarlyType;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.DefaultGreedyDecider;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.GreedyDecider;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.forager.GreedyForager;
import org.drools.planner.core.constructionheuristic.greedyFit.selector.GreedyPlanningEntitySelector;
import org.drools.planner.core.constructionheuristic.placer.entity.EntityPlacer;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.entity.PlanningEntitySelectionOrder;
import org.drools.planner.core.heuristic.selector.entity.PlanningEntitySelector;
import org.drools.planner.core.heuristic.selector.variable.PlanningValueSelectionOrder;
import org.drools.planner.core.heuristic.selector.variable.PlanningValueSelector;
import org.drools.planner.core.heuristic.selector.variable.PlanningValueWalker;
import org.drools.planner.core.heuristic.selector.variable.PlanningVariableWalker;
import org.drools.planner.core.phase.AbstractSolverPhase;
import org.drools.planner.core.phase.SolverPhase;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.termination.Termination;

@XStreamAlias("constructionHeuristic")
public class ConstructionHeuristicSolverPhaseConfig extends SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected ConstructionHeuristicType constructionHeuristicType = null;
    protected ConstructionHeuristicPickEarlyType constructionHeuristicPickEarlyType = null;

    @XStreamImplicit
    protected List<EntityPlacerConfig> entityPlacerConfigList = null;

    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    public void setConstructionHeuristicType(ConstructionHeuristicType constructionHeuristicType) {
        this.constructionHeuristicType = constructionHeuristicType;
    }

    public ConstructionHeuristicPickEarlyType getConstructionHeuristicPickEarlyType() {
        return constructionHeuristicPickEarlyType;
    }

    public void setConstructionHeuristicPickEarlyType(ConstructionHeuristicPickEarlyType constructionHeuristicPickEarlyType) {
        this.constructionHeuristicPickEarlyType = constructionHeuristicPickEarlyType;
    }

    public List<EntityPlacerConfig> getEntityPlacerConfigList() {
        return entityPlacerConfigList;
    }

    public void setEntityPlacerConfigList(List<EntityPlacerConfig> entityPlacerConfigList) {
        this.entityPlacerConfigList = entityPlacerConfigList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************


    // TODO downcast return type SolverPhase
    public SolverPhase buildSolverPhase(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            ScoreDefinition scoreDefinition, Termination solverTermination) {
        if (constructionHeuristicType != null) {
            // TODO delete GreedyFitSolverPhase
            DefaultGreedyFitSolverPhase greedySolverPhase = new DefaultGreedyFitSolverPhase();
            configureSolverPhase(greedySolverPhase, environmentMode, scoreDefinition, solverTermination);
            greedySolverPhase.setGreedyPlanningEntitySelector(buildGreedyPlanningEntitySelector(solutionDescriptor));
            greedySolverPhase.setGreedyDecider(buildGreedyDecider(solutionDescriptor, environmentMode));
            if (environmentMode == EnvironmentMode.DEBUG || environmentMode == EnvironmentMode.TRACE) {
                greedySolverPhase.setAssertStepScoreIsUncorrupted(true);
            }
            return greedySolverPhase;
        } else if (!CollectionUtils.isEmpty(entityPlacerConfigList)) {
            if (constructionHeuristicPickEarlyType != null) {
                // TODO throw decent exception
                throw new UnsupportedOperationException();
            }


            DefaultConstructionHeuristicSolverPhase phase = new DefaultConstructionHeuristicSolverPhase();
            configureSolverPhase(phase, environmentMode, scoreDefinition, solverTermination);

            List<EntityPlacer> entityPlacerList = new ArrayList<EntityPlacer>(entityPlacerConfigList.size());
            for (EntityPlacerConfig entityPlacerConfig : entityPlacerConfigList) {
                EntityPlacer entityPlacer = entityPlacerConfig.buildEntityPlacer(
                        environmentMode, solutionDescriptor, phase.getTermination());
                entityPlacerList.add(entityPlacer);
            }
            phase.setEntityPlacerList(entityPlacerList);
            if (environmentMode == EnvironmentMode.DEBUG || environmentMode == EnvironmentMode.TRACE) {
                phase.setAssertStepScoreIsUncorrupted(true);
            }
            return phase;
        } else {
            throw new IllegalArgumentException("A constructionHeuristic requires configuration, " +
                    "for example a constructionHeuristicType.");
        }
    }

    @Deprecated
    private GreedyPlanningEntitySelector buildGreedyPlanningEntitySelector(SolutionDescriptor solutionDescriptor) {
        GreedyPlanningEntitySelector greedyPlanningEntitySelector = new GreedyPlanningEntitySelector();

        Set<Class<?>> planningEntityClassSet = solutionDescriptor.getPlanningEntityClassSet();
        if (planningEntityClassSet.size() != 1) {
            // TODO Multiple MUST BE SUPPORTED TOO
            throw new UnsupportedOperationException("Currently the greedyFit implementation only supports " +
                    "1 planningEntityClass.");
        }
        Class<?> planningEntityClass = planningEntityClassSet.iterator().next();
        List<PlanningEntitySelector> planningEntitySelectorList = new ArrayList<PlanningEntitySelector>(1);
        PlanningEntitySelector planningEntitySelector = new PlanningEntitySelector(
                solutionDescriptor.getPlanningEntityDescriptor(planningEntityClass));
        planningEntitySelector.setSelectionOrder(determinePlanningEntitySelectionOrder());
        planningEntitySelector.setResetInitializedPlanningEntities(false);
        planningEntitySelectorList.add(planningEntitySelector);
        greedyPlanningEntitySelector.setPlanningEntitySelectorList(planningEntitySelectorList);
        return greedyPlanningEntitySelector;
    }

    @Deprecated
    private GreedyDecider buildGreedyDecider(SolutionDescriptor solutionDescriptor, EnvironmentMode environmentMode) {
        DefaultGreedyDecider greedyDecider = new DefaultGreedyDecider();

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
            planningValueSelector.setSelectionOrder(determinePlanningValueSelectionOrder());
            PlanningValueWalker planningValueWalker = new PlanningValueWalker(
                    planningVariableDescriptor, planningValueSelector);
            planningValueWalkerList.add(planningValueWalker);
        }
        planningVariableWalker.setPlanningValueWalkerList(planningValueWalkerList);
        greedyDecider.setPlanningVariableWalker(planningVariableWalker);
        
        greedyDecider.setForager(buildGreedyForager());
        if (environmentMode == EnvironmentMode.TRACE) {
            greedyDecider.setAssertMoveScoreIsUncorrupted(true);
        }
        if (environmentMode == EnvironmentMode.DEBUG || environmentMode == EnvironmentMode.TRACE) {
            greedyDecider.setAssertUndoMoveIsUncorrupted(true);
        }
        return greedyDecider;
    }

    @Deprecated
    private GreedyForager buildGreedyForager() {
        GreedyForager forager = new GreedyForager();
        ConstructionHeuristicPickEarlyType pickEarlyType = (this.constructionHeuristicPickEarlyType == null)
                ? ConstructionHeuristicPickEarlyType.NEVER : this.constructionHeuristicPickEarlyType;
        forager.setPickEarlyType(pickEarlyType);
        return forager;
    }

    private PlanningEntitySelectionOrder determinePlanningEntitySelectionOrder() {
        switch (constructionHeuristicType) {
            case FIRST_FIT:
            case BEST_FIT:
                return PlanningEntitySelectionOrder.ORIGINAL;
            case FIRST_FIT_DECREASING:
            case BEST_FIT_DECREASING:
                return PlanningEntitySelectionOrder.DECREASING_DIFFICULTY;
            default:
                throw new IllegalStateException("The constructionHeuristicType ("
                        + constructionHeuristicType + ") is not implemented");
        }
    }

    private PlanningValueSelectionOrder determinePlanningValueSelectionOrder() {
        switch (constructionHeuristicType) {
            case FIRST_FIT:
            case FIRST_FIT_DECREASING:
                return PlanningValueSelectionOrder.ORIGINAL;
            case BEST_FIT:
            case BEST_FIT_DECREASING:
                return PlanningValueSelectionOrder.INCREASING_STRENGTH;
            default:
                throw new IllegalStateException("The constructionHeuristicType ("
                        + constructionHeuristicType + ") is not implemented");
        }
    }

    public void inherit(ConstructionHeuristicSolverPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (constructionHeuristicType == null) {
            constructionHeuristicType = inheritedConfig.getConstructionHeuristicType();
        }
        if (constructionHeuristicPickEarlyType == null) {
            constructionHeuristicPickEarlyType = inheritedConfig.getConstructionHeuristicPickEarlyType();
        }
    }

    public static enum ConstructionHeuristicType {
        FIRST_FIT,
        FIRST_FIT_DECREASING,
        BEST_FIT,
        BEST_FIT_DECREASING
    }

}
