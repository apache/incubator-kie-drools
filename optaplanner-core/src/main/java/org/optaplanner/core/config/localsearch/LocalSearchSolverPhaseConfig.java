/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.config.localsearch;

import java.util.Arrays;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.ForagerConfig;
import org.optaplanner.core.config.phase.SolverPhaseConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.localsearch.DefaultLocalSearchSolverPhase;
import org.optaplanner.core.impl.localsearch.LocalSearchSolverPhase;
import org.optaplanner.core.impl.localsearch.decider.Decider;
import org.optaplanner.core.impl.localsearch.decider.DefaultDecider;
import org.optaplanner.core.impl.localsearch.decider.forager.Forager;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.termination.Termination;

@XStreamAlias("localSearch")
public class LocalSearchSolverPhaseConfig extends SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    // TODO This is a List due to XStream limitations. With JAXB it could be just a MoveSelectorConfig instead.
    @XStreamImplicit()
    private List<MoveSelectorConfig> moveSelectorConfigList = null;
    @XStreamAlias("acceptor")
    private AcceptorConfig acceptorConfig = null;
    @XStreamAlias("forager")
    private ForagerConfig foragerConfig = null;

    public List<MoveSelectorConfig> getMoveSelectorConfigList() {
        return moveSelectorConfigList;
    }

    public void setMoveSelectorConfigList(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
    }

    public AcceptorConfig getAcceptorConfig() {
        return acceptorConfig;
    }

    public void setAcceptorConfig(AcceptorConfig acceptorConfig) {
        this.acceptorConfig = acceptorConfig;
    }

    public ForagerConfig getForagerConfig() {
        return foragerConfig;
    }

    public void setForagerConfig(ForagerConfig foragerConfig) {
        this.foragerConfig = foragerConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public LocalSearchSolverPhase buildSolverPhase(int phaseIndex, EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor, ScoreDefinition scoreDefinition, Termination solverTermination) {
        DefaultLocalSearchSolverPhase localSearchSolverPhase = new DefaultLocalSearchSolverPhase();
        configureSolverPhase(localSearchSolverPhase, phaseIndex, environmentMode, scoreDefinition, solverTermination);
        localSearchSolverPhase.setDecider(buildDecider(environmentMode, solutionDescriptor, scoreDefinition,
                localSearchSolverPhase.getTermination()));
        if (environmentMode == EnvironmentMode.FAST_ASSERT || environmentMode == EnvironmentMode.FULL_ASSERT) {
            localSearchSolverPhase.setAssertStepScoreIsUncorrupted(true);
        }
        return localSearchSolverPhase;
    }

    private Decider buildDecider(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            ScoreDefinition scoreDefinition, Termination phaseTermination) {
        DefaultDecider decider = new DefaultDecider();
        decider.setTermination(phaseTermination);
        MoveSelector moveSelector = buildMoveSelector(environmentMode, solutionDescriptor);
        decider.setMoveSelector(moveSelector);
        AcceptorConfig acceptorConfig_ = acceptorConfig == null ? new AcceptorConfig()
                : acceptorConfig;
        decider.setAcceptor(acceptorConfig_.buildAcceptor(environmentMode, scoreDefinition));
        ForagerConfig foragerConfig_ = foragerConfig == null ? new ForagerConfig()
                : foragerConfig;
        Forager forager = foragerConfig_.buildForager(scoreDefinition);
        decider.setForager(forager);
        if (moveSelector.isNeverEnding() && !forager.supportsNeverEndingMoveSelector()) {
            throw new IllegalStateException("The moveSelector (" + moveSelector
                    + ") has neverEnding (" + moveSelector.isNeverEnding()
                    + "), but the forager (" + forager
                    + ") does not support it."
                    + " Configure the <forager> with <minimalAcceptedSelection>.");
        }
        if (environmentMode == EnvironmentMode.FULL_ASSERT) {
            decider.setAssertMoveScoreIsUncorrupted(true);
        }
        if (environmentMode == EnvironmentMode.FAST_ASSERT || environmentMode == EnvironmentMode.FULL_ASSERT) {
            decider.setAssertUndoMoveIsUncorrupted(true);
        }
        return decider;
    }

    private MoveSelector buildMoveSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor) {
        MoveSelector moveSelector;
        SelectionCacheType defaultCacheType = SelectionCacheType.JUST_IN_TIME;
        SelectionOrder defaultSelectionOrder = SelectionOrder.RANDOM;
        if (CollectionUtils.isEmpty(moveSelectorConfigList)) {
            // Default to changeMoveSelector and swapMoveSelector
            UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
            unionMoveSelectorConfig.setMoveSelectorConfigList(Arrays.asList(
                    new ChangeMoveSelectorConfig(), new SwapMoveSelectorConfig()));
            moveSelector = unionMoveSelectorConfig.buildMoveSelector(environmentMode, solutionDescriptor,
                    defaultCacheType, defaultSelectionOrder);
        } else if (moveSelectorConfigList.size() == 1) {
            moveSelector = moveSelectorConfigList.get(0).buildMoveSelector(
                    environmentMode, solutionDescriptor, defaultCacheType, defaultSelectionOrder);
        } else {
            // TODO moveSelectorConfigList is only a List because of XStream limitations.
            throw new IllegalArgumentException("The moveSelectorConfigList (" + moveSelectorConfigList
                    + ") must be a singleton or empty. Use a single " + UnionMoveSelectorConfig.class
                    // TODO + " or " + CartesianProductMoveSelectorConfig.class
                    + " element to nest multiple MoveSelectors.");
        }
        return moveSelector;
    }

    public void inherit(LocalSearchSolverPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        moveSelectorConfigList = ConfigUtils.inheritMergeableListProperty(moveSelectorConfigList,
                inheritedConfig.getMoveSelectorConfigList());
        if (acceptorConfig == null) {
            acceptorConfig = inheritedConfig.getAcceptorConfig();
        } else if (inheritedConfig.getAcceptorConfig() != null) {
            acceptorConfig.inherit(inheritedConfig.getAcceptorConfig());
        }
        if (foragerConfig == null) {
            foragerConfig = inheritedConfig.getForagerConfig();
        } else if (inheritedConfig.getForagerConfig() != null) {
            foragerConfig.inherit(inheritedConfig.getForagerConfig());
        }
    }

}
