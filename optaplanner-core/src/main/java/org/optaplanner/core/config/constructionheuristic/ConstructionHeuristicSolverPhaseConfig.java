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

package org.optaplanner.core.config.constructionheuristic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.constructionheuristic.placer.EntityPlacerConfig;
import org.optaplanner.core.config.phase.SolverPhaseConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.constructionheuristic.ConstructionHeuristicSolverPhase;
import org.optaplanner.core.impl.constructionheuristic.DefaultConstructionHeuristicSolverPhase;
import org.optaplanner.core.impl.constructionheuristic.decider.ConstructionHeuristicDecider;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.DefaultGreedyFitSolverPhase;
import org.optaplanner.core.impl.constructionheuristic.decider.ConstructionHeuristicPickEarlyType;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.decider.DefaultGreedyDecider;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.decider.GreedyDecider;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.decider.forager.GreedyForager;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.selector.GreedyPlanningEntitySelector;
import org.optaplanner.core.impl.constructionheuristic.placer.EntityPlacer;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.entity.PlanningEntitySelectionOrder;
import org.optaplanner.core.impl.heuristic.selector.entity.PlanningEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningValueSelectionOrder;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningValueSelector;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningValueWalker;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningVariableWalker;
import org.optaplanner.core.impl.termination.Termination;

@XStreamAlias("constructionHeuristic")
public class ConstructionHeuristicSolverPhaseConfig extends SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected ConstructionHeuristicType constructionHeuristicType = null;
    protected ConstructionHeuristicPickEarlyType pickEarlyType = null;

    // TODO This is a List due to XStream limitations. With JAXB it could be just a EntityPlacerConfig instead.
    @XStreamImplicit
    protected List<EntityPlacerConfig> entityPlacerConfigList = null;

    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    public void setConstructionHeuristicType(ConstructionHeuristicType constructionHeuristicType) {
        this.constructionHeuristicType = constructionHeuristicType;
    }

    public ConstructionHeuristicPickEarlyType getPickEarlyType() {
        return pickEarlyType;
    }

    public void setPickEarlyType(ConstructionHeuristicPickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
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


    public ConstructionHeuristicSolverPhase buildSolverPhase(int phaseIndex, HeuristicConfigPolicy solverConfigPolicy,
            Termination solverTermination) {
        HeuristicConfigPolicy phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        phaseConfigPolicy.setInitializedChainedValueFilterEnabled(true);
        if (!CollectionUtils.isEmpty(entityPlacerConfigList)) {
            if (pickEarlyType != null) {
                // TODO throw decent exception
                throw new UnsupportedOperationException();
            }
            ConstructionHeuristicType constructionHeuristicType_ = constructionHeuristicType == null
                    ? ConstructionHeuristicType.FIRST_FIT : constructionHeuristicType;
            phaseConfigPolicy.setSortEntitiesByDecreasingDifficultyEnabled(
                    constructionHeuristicType_.isSortEntitiesByDecreasingDifficulty());
            phaseConfigPolicy.setSortValuesByIncreasingStrengthEnabled(
                    constructionHeuristicType_.isSortValuesByIncreasingStrength());
            DefaultConstructionHeuristicSolverPhase phase = new DefaultConstructionHeuristicSolverPhase();
            configureSolverPhase(phase, phaseIndex, phaseConfigPolicy, solverTermination);
            phase.setDecider(buildDecider(phaseConfigPolicy, phase.getTermination()));
            EntityPlacer entityPlacer;
            if (entityPlacerConfigList.size() == 1) {
                entityPlacer = entityPlacerConfigList.get(0).buildEntityPlacer(
                        phaseConfigPolicy, phase.getTermination());
            } else {
                // TODO entityPlacerConfigList is only a List because of XStream limitations.
                throw new IllegalArgumentException("The entityPlacerConfigList (" + entityPlacerConfigList
                        + ") must be a singleton or empty. Use multiple " + ConstructionHeuristicSolverPhaseConfig.class
                        + " elements to initialize multiple entity classes.");
            }
            phase.setEntityPlacer(entityPlacer);
            EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
            if (environmentMode.isNonIntrusiveFullAsserted()) {
                phase.setAssertStepScoreFromScratch(true);
            }
            if (environmentMode.isIntrusiveFastAsserted()) {
                phase.setAssertExpectedStepScore(true);
            }
            return phase;
        } else if (constructionHeuristicType != null) {
            // TODO delete this legacy piece for GreedyFitSolverPhase
            DefaultGreedyFitSolverPhase greedySolverPhase = new DefaultGreedyFitSolverPhase();
            configureSolverPhase(greedySolverPhase, phaseIndex, phaseConfigPolicy, solverTermination);
            greedySolverPhase.setGreedyPlanningEntitySelector(buildGreedyPlanningEntitySelector(
                    solverConfigPolicy.getSolutionDescriptor()));
            greedySolverPhase.setGreedyDecider(buildGreedyDecider(
                    solverConfigPolicy.getSolutionDescriptor(), solverConfigPolicy.getEnvironmentMode()));
            EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
            if (environmentMode.isNonIntrusiveFullAsserted()) {
                greedySolverPhase.setAssertStepScoreFromScratch(true);
            }
            if (environmentMode.isIntrusiveFastAsserted()) {
                greedySolverPhase.setAssertExpectedStepScore(true);
            }
            return greedySolverPhase;
        } else {
            throw new IllegalArgumentException("A constructionHeuristic requires configuration, " +
                    "for example a constructionHeuristicType.");
        }
    }

    private ConstructionHeuristicDecider buildDecider(HeuristicConfigPolicy configPolicy, Termination termination) {
        ConstructionHeuristicDecider decider = new ConstructionHeuristicDecider(termination);
        EnvironmentMode environmentMode = configPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            decider.setAssertMoveScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            decider.setAssertExpectedUndoMoveScore(true);
        }
        return decider;
    }

    @Deprecated
    private GreedyPlanningEntitySelector buildGreedyPlanningEntitySelector(SolutionDescriptor solutionDescriptor) {
        GreedyPlanningEntitySelector greedyPlanningEntitySelector = new GreedyPlanningEntitySelector();

        Collection<PlanningEntityDescriptor> entityDescriptors = solutionDescriptor.getGenuineEntityDescriptors();
        if (entityDescriptors.size() != 1) {
            throw new UnsupportedOperationException("Currently the greedyFit implementation only supports " +
                    "1 planningEntityClass.");
        }
        PlanningEntityDescriptor entityDescriptor = entityDescriptors.iterator().next();
        List<PlanningEntitySelector> planningEntitySelectorList = new ArrayList<PlanningEntitySelector>(1);
        PlanningEntitySelector planningEntitySelector = new PlanningEntitySelector(entityDescriptor);
        planningEntitySelector.setSelectionOrder(determinePlanningEntitySelectionOrder());
        planningEntitySelectorList.add(planningEntitySelector);
        greedyPlanningEntitySelector.setPlanningEntitySelectorList(planningEntitySelectorList);
        return greedyPlanningEntitySelector;
    }

    @Deprecated
    private GreedyDecider buildGreedyDecider(SolutionDescriptor solutionDescriptor, EnvironmentMode environmentMode) {
        DefaultGreedyDecider greedyDecider = new DefaultGreedyDecider();

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
            planningValueSelector.setSelectionOrder(determinePlanningValueSelectionOrder());
            PlanningValueWalker planningValueWalker = new PlanningValueWalker(
                    variableDescriptor, planningValueSelector);
            planningValueWalkerList.add(planningValueWalker);
        }
        planningVariableWalker.setPlanningValueWalkerList(planningValueWalkerList);
        greedyDecider.setPlanningVariableWalker(planningVariableWalker);
        
        greedyDecider.setForager(buildGreedyForager());
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            greedyDecider.setAssertMoveScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            greedyDecider.setAssertExpectedUndoMoveScore(true);
        }
        return greedyDecider;
    }

    @Deprecated
    private GreedyForager buildGreedyForager() {
        GreedyForager forager = new GreedyForager();
        ConstructionHeuristicPickEarlyType pickEarlyType_ = (this.pickEarlyType == null)
                ? ConstructionHeuristicPickEarlyType.NEVER : this.pickEarlyType;
        forager.setPickEarlyType(pickEarlyType_);
        return forager;
    }

    @Deprecated
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
                        + constructionHeuristicType + ") is not implemented.");
        }
    }

    @Deprecated
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
                        + constructionHeuristicType + ") is not implemented.");
        }
    }

    public void inherit(ConstructionHeuristicSolverPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (constructionHeuristicType == null) {
            constructionHeuristicType = inheritedConfig.getConstructionHeuristicType();
        }
        if (pickEarlyType == null) {
            pickEarlyType = inheritedConfig.getPickEarlyType();
        }
        entityPlacerConfigList = ConfigUtils.inheritMergeableListProperty(
                entityPlacerConfigList, inheritedConfig.getEntityPlacerConfigList());
    }

    public static enum ConstructionHeuristicType {
        FIRST_FIT,
        FIRST_FIT_DECREASING,
        BEST_FIT,
        BEST_FIT_DECREASING;

        public boolean isSortEntitiesByDecreasingDifficulty() {
            switch (this) {
                case FIRST_FIT:
                case BEST_FIT:
                    return false;
                case FIRST_FIT_DECREASING:
                case BEST_FIT_DECREASING:
                    return true;
                default:
                    throw new IllegalStateException("The constructionHeuristicType ("
                            + this + ") is not implemented.");
            }
        }

        public boolean isSortValuesByIncreasingStrength() {
            switch (this) {
                case FIRST_FIT:
                case FIRST_FIT_DECREASING:
                    return false;
                case BEST_FIT:
                case BEST_FIT_DECREASING:
                    return true;
                default:
                    throw new IllegalStateException("The constructionHeuristicType ("
                            + this + ") is not implemented.");
            }
        }
    }

}
