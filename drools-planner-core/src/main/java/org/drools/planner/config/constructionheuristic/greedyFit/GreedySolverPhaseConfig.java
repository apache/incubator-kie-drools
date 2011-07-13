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

import java.util.Comparator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.phase.SolverPhaseConfig;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.core.constructionheuristic.greedyFit.DefaultGreedySolverPhase;
import org.drools.planner.core.constructionheuristic.greedyFit.GreedySolverPhase;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.DefaultGreedyDecider;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.GreedyDecider;
import org.drools.planner.core.constructionheuristic.greedyFit.decider.PickEarlyFitType;
import org.drools.planner.core.constructionheuristic.greedyFit.selector.GreedyPlanningEntitySelector;
import org.drools.planner.core.domain.entity.PlanningEntityDifficultyWeightFactory;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.termination.Termination;

@XStreamAlias("greedy")
public class GreedySolverPhaseConfig extends SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    private Boolean resetInitializedPlanningEntities = null;
    private Class<? extends Comparator<Object>> fitOrderPlanningEntityComparatorClass = null;
    private Class<? extends PlanningEntityDifficultyWeightFactory> planningEntityDifficultyWeightFactoryClass = null;
    private PickEarlyFitType pickEarlyFitType = null;

    public Boolean getResetInitializedPlanningEntities() {
        return resetInitializedPlanningEntities;
    }

    public void setResetInitializedPlanningEntities(Boolean resetInitializedPlanningEntities) {
        this.resetInitializedPlanningEntities = resetInitializedPlanningEntities;
    }

    public Class<? extends Comparator<Object>> getFitOrderPlanningEntityComparatorClass() {
        return fitOrderPlanningEntityComparatorClass;
    }

    public void setFitOrderPlanningEntityComparatorClass(Class<? extends Comparator<Object>> fitOrderPlanningEntityComparatorClass) {
        this.fitOrderPlanningEntityComparatorClass = fitOrderPlanningEntityComparatorClass;
    }

    public Class<? extends PlanningEntityDifficultyWeightFactory> getPlanningEntityDifficultyWeightFactoryClass() {
        return planningEntityDifficultyWeightFactoryClass;
    }

    public void setPlanningEntityDifficultyWeightFactoryClass(Class<? extends PlanningEntityDifficultyWeightFactory> planningEntityDifficultyWeightFactoryClass) {
        this.planningEntityDifficultyWeightFactoryClass = planningEntityDifficultyWeightFactoryClass;
    }

    public PickEarlyFitType getPickEarlyFitType() {
        return pickEarlyFitType;
    }

    public void setPickEarlyFitType(PickEarlyFitType pickEarlyFitType) {
        this.pickEarlyFitType = pickEarlyFitType;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public GreedySolverPhase buildSolverPhase(EnvironmentMode environmentMode, ScoreDefinition scoreDefinition,
            Termination solverTermination) {
        DefaultGreedySolverPhase greedySolverPhase = new DefaultGreedySolverPhase();
        configureSolverPhase(greedySolverPhase, environmentMode, scoreDefinition, solverTermination);
        greedySolverPhase.setGreedyPlanningEntitySelector(buildGreedyPlanningEntitySelector());
        greedySolverPhase.setGreedyDecider(buildGreedyDecider(environmentMode));
        if (environmentMode == EnvironmentMode.DEBUG || environmentMode == EnvironmentMode.TRACE) {
            greedySolverPhase.setAssertStepScoreIsUncorrupted(true);
        }
        return greedySolverPhase;
    }

    private GreedyPlanningEntitySelector buildGreedyPlanningEntitySelector() {
        GreedyPlanningEntitySelector greedyPlanningEntitySelector = new GreedyPlanningEntitySelector();
        boolean resetInitializedPlanningEntitiesValue = resetInitializedPlanningEntities == null ? false
                : resetInitializedPlanningEntities.booleanValue();
        greedyPlanningEntitySelector.setResetInitializedPlanningEntities(resetInitializedPlanningEntitiesValue);
        if (fitOrderPlanningEntityComparatorClass != null && planningEntityDifficultyWeightFactoryClass != null) {
            throw new IllegalArgumentException("Cannot configure fitOrderPlanningEntityComparatorClass ("
                    + fitOrderPlanningEntityComparatorClass
                    + ") and planningEntityDifficultyWeightFactoryClass (" + planningEntityDifficultyWeightFactoryClass
                    + ") at the same time.");
        }
        if (fitOrderPlanningEntityComparatorClass != null) {
            Comparator<Object> fitOrderPlanningEntityComparator;
            try {
                fitOrderPlanningEntityComparator = fitOrderPlanningEntityComparatorClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("The fitOrderPlanningEntityComparatorClass ("
                        + fitOrderPlanningEntityComparatorClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("The fitOrderPlanningEntityComparatorClass ("
                        + fitOrderPlanningEntityComparatorClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            greedyPlanningEntitySelector.setFitOrderPlanningEntityComparator(fitOrderPlanningEntityComparator);
        }
        if (planningEntityDifficultyWeightFactoryClass != null) {
            PlanningEntityDifficultyWeightFactory planningEntityDifficultyWeightFactory;
            try {
                planningEntityDifficultyWeightFactory = planningEntityDifficultyWeightFactoryClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("The planningEntityDifficultyWeightFactoryClass ("
                        + planningEntityDifficultyWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("The planningEntityDifficultyWeightFactoryClass ("
                        + planningEntityDifficultyWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            greedyPlanningEntitySelector.setPlanningEntityDifficultyWeightFactory(
                    planningEntityDifficultyWeightFactory);
        }
        return greedyPlanningEntitySelector;
    }

    private GreedyDecider buildGreedyDecider(EnvironmentMode environmentMode) {
        DefaultGreedyDecider greedyDecider = new DefaultGreedyDecider();
        PickEarlyFitType pickEarlyFitType = (this.pickEarlyFitType == null)
                ? PickEarlyFitType.NEVER : this.pickEarlyFitType;
        greedyDecider.setPickEarlyFitType(pickEarlyFitType);
        if (environmentMode == EnvironmentMode.TRACE) {
            greedyDecider.setAssertMoveScoreIsUncorrupted(true);
        }
        return greedyDecider;
    }

    public void inherit(GreedySolverPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (resetInitializedPlanningEntities == null) {
            resetInitializedPlanningEntities = inheritedConfig.getResetInitializedPlanningEntities();
        }
        if (fitOrderPlanningEntityComparatorClass == null) {
            fitOrderPlanningEntityComparatorClass = inheritedConfig.getFitOrderPlanningEntityComparatorClass();
        }
        if (planningEntityDifficultyWeightFactoryClass == null) {
            planningEntityDifficultyWeightFactoryClass = inheritedConfig
                    .getPlanningEntityDifficultyWeightFactoryClass();
        }
        if (pickEarlyFitType == null) {
            pickEarlyFitType = inheritedConfig.getPickEarlyFitType();
        }
    }

}
