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

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicForagerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.EntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedEntityPlacerConfig;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.phase.SolverPhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.constructionheuristic.ConstructionHeuristicSolverPhase;
import org.optaplanner.core.impl.constructionheuristic.DefaultConstructionHeuristicSolverPhase;
import org.optaplanner.core.impl.constructionheuristic.decider.ConstructionHeuristicDecider;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.ConstructionHeuristicForager;
import org.optaplanner.core.impl.constructionheuristic.placer.EntityPlacer;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;

@XStreamAlias("constructionHeuristic")
public class ConstructionHeuristicSolverPhaseConfig extends SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected ConstructionHeuristicType constructionHeuristicType = null;

    // TODO This is a List due to XStream limitations. With JAXB it could be just a EntityPlacerConfig instead.
    @XStreamImplicit
    protected List<EntityPlacerConfig> entityPlacerConfigList = null;

    @XStreamAlias("forager")
    protected ConstructionHeuristicForagerConfig foragerConfig = null;

    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    public void setConstructionHeuristicType(ConstructionHeuristicType constructionHeuristicType) {
        this.constructionHeuristicType = constructionHeuristicType;
    }

    public List<EntityPlacerConfig> getEntityPlacerConfigList() {
        return entityPlacerConfigList;
    }

    public void setEntityPlacerConfigList(List<EntityPlacerConfig> entityPlacerConfigList) {
        this.entityPlacerConfigList = entityPlacerConfigList;
    }

    public ConstructionHeuristicForagerConfig getForagerConfig() {
        return foragerConfig;
    }

    public void setForagerConfig(ConstructionHeuristicForagerConfig foragerConfig) {
        this.foragerConfig = foragerConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************


    public ConstructionHeuristicSolverPhase buildSolverPhase(int phaseIndex, HeuristicConfigPolicy solverConfigPolicy,
            BestSolutionRecaller bestSolutionRecaller, Termination solverTermination) {
        HeuristicConfigPolicy phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        phaseConfigPolicy.setReinitializeVariableFilterEnabled(true);
        phaseConfigPolicy.setInitializedChainedValueFilterEnabled(true);
        ConstructionHeuristicType constructionHeuristicType_ = constructionHeuristicType == null
                ? ConstructionHeuristicType.FIRST_FIT : constructionHeuristicType;
        phaseConfigPolicy.setSortEntitiesByDecreasingDifficultyEnabled(
                constructionHeuristicType_.isSortEntitiesByDecreasingDifficulty());
        phaseConfigPolicy.setSortValuesByIncreasingStrengthEnabled(
                constructionHeuristicType_.isSortValuesByIncreasingStrength());
        DefaultConstructionHeuristicSolverPhase phase = new DefaultConstructionHeuristicSolverPhase();
        configureSolverPhase(phase, phaseIndex, phaseConfigPolicy, bestSolutionRecaller, solverTermination);
        phase.setDecider(buildDecider(phaseConfigPolicy, phase.getTermination()));
        EntityPlacerConfig entityPlacerConfig;
        if (ConfigUtils.isEmptyCollection(entityPlacerConfigList)) {
            entityPlacerConfig = new QueuedEntityPlacerConfig();
        } else if (entityPlacerConfigList.size() == 1) {
            entityPlacerConfig = entityPlacerConfigList.get(0);
        } else {
            // TODO entityPlacerConfigList is only a List because of XStream limitations.
            throw new IllegalArgumentException("The entityPlacerConfigList (" + entityPlacerConfigList
                    + ") must be a singleton or empty. Use multiple " + ConstructionHeuristicSolverPhaseConfig.class
                    + " elements to initialize multiple entity classes.");
        }
        EntityPlacer entityPlacer = entityPlacerConfig.buildEntityPlacer(
                phaseConfigPolicy, phase.getTermination());
        phase.setEntityPlacer(entityPlacer);
        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            phase.setAssertStepScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            phase.setAssertExpectedStepScore(true);
        }
        return phase;
    }

    private ConstructionHeuristicDecider buildDecider(HeuristicConfigPolicy configPolicy, Termination termination) {
        ConstructionHeuristicForagerConfig foragerConfig_ = foragerConfig == null
                ? new ConstructionHeuristicForagerConfig() : foragerConfig;
        ConstructionHeuristicForager forager = foragerConfig_.buildForager(configPolicy);
        ConstructionHeuristicDecider decider = new ConstructionHeuristicDecider(termination, forager);
        EnvironmentMode environmentMode = configPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            decider.setAssertMoveScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            decider.setAssertExpectedUndoMoveScore(true);
        }
        return decider;
    }

    public void inherit(ConstructionHeuristicSolverPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        constructionHeuristicType = ConfigUtils.inheritOverwritableProperty(constructionHeuristicType,
                inheritedConfig.getConstructionHeuristicType());
        entityPlacerConfigList = ConfigUtils.inheritMergeableListProperty(
                entityPlacerConfigList, inheritedConfig.getEntityPlacerConfigList());
        if (foragerConfig == null) {
            foragerConfig = inheritedConfig.getForagerConfig();
        } else if (inheritedConfig.getForagerConfig() != null) {
            foragerConfig.inherit(inheritedConfig.getForagerConfig());
        }
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
