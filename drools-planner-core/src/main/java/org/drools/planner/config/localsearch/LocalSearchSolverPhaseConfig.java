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

package org.drools.planner.config.localsearch;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.phase.SolverPhaseConfig;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.localsearch.decider.acceptor.AcceptorConfig;
import org.drools.planner.config.localsearch.decider.deciderscorecomparator.DeciderScoreComparatorFactoryConfig;
import org.drools.planner.config.localsearch.decider.forager.ForagerConfig;
import org.drools.planner.config.localsearch.decider.selector.SelectorConfig;
import org.drools.planner.config.termination.TerminationConfig;
import org.drools.planner.core.localsearch.DefaultLocalSearchSolverPhase;
import org.drools.planner.core.localsearch.LocalSearchSolverPhase;
import org.drools.planner.core.localsearch.decider.Decider;
import org.drools.planner.core.localsearch.decider.DefaultDecider;
import org.drools.planner.core.score.definition.ScoreDefinition;

@XStreamAlias("localSearch")
public class LocalSearchSolverPhaseConfig extends SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XStreamAlias("deciderScoreComparatorFactory")
    private DeciderScoreComparatorFactoryConfig deciderScoreComparatorFactoryConfig
            = new DeciderScoreComparatorFactoryConfig();
    @XStreamAlias("selector")
    private SelectorConfig selectorConfig = new SelectorConfig();
    @XStreamAlias("acceptor")
    private AcceptorConfig acceptorConfig = new AcceptorConfig();
    @XStreamAlias("forager")
    private ForagerConfig foragerConfig = new ForagerConfig();

    public DeciderScoreComparatorFactoryConfig getDeciderScoreComparatorFactoryConfig() {
        return deciderScoreComparatorFactoryConfig;
    }

    public void setDeciderScoreComparatorFactoryConfig(
            DeciderScoreComparatorFactoryConfig deciderScoreComparatorFactoryConfig) {
        this.deciderScoreComparatorFactoryConfig = deciderScoreComparatorFactoryConfig;
    }

    public SelectorConfig getSelectorConfig() {
        return selectorConfig;
    }

    public void setSelectorConfig(SelectorConfig selectorConfig) {
        this.selectorConfig = selectorConfig;
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

    public LocalSearchSolverPhase buildSolverPhase(EnvironmentMode environmentMode, ScoreDefinition scoreDefinition) {
        DefaultLocalSearchSolverPhase localSearchSolverPhase = new DefaultLocalSearchSolverPhase();
        configureSolverPhase(localSearchSolverPhase, environmentMode, scoreDefinition);
        localSearchSolverPhase.setDecider(buildDecider(environmentMode, scoreDefinition));
        if (environmentMode == EnvironmentMode.DEBUG || environmentMode == EnvironmentMode.TRACE) {
            localSearchSolverPhase.setAssertStepScoreIsUncorrupted(true);
        }
        return localSearchSolverPhase;
    }

    private Decider buildDecider(EnvironmentMode environmentMode, ScoreDefinition scoreDefinition) {
        DefaultDecider decider = new DefaultDecider();
        decider.setDeciderScoreComparator(deciderScoreComparatorFactoryConfig.buildDeciderScoreComparatorFactory());
        decider.setSelector(selectorConfig.buildSelector(scoreDefinition));
        decider.setAcceptor(acceptorConfig.buildAcceptor(environmentMode, scoreDefinition));
        decider.setForager(foragerConfig.buildForager(scoreDefinition));
        if (environmentMode == EnvironmentMode.TRACE) {
            decider.setAssertMoveScoreIsUncorrupted(true);
        }
        if (environmentMode == EnvironmentMode.DEBUG || environmentMode == EnvironmentMode.TRACE) {
            decider.setAssertUndoMoveIsUncorrupted(true);
        }
        return decider;
    }

    public void inherit(LocalSearchSolverPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (deciderScoreComparatorFactoryConfig == null) {
            deciderScoreComparatorFactoryConfig = inheritedConfig.getDeciderScoreComparatorFactoryConfig();
        } else if (inheritedConfig.getDeciderScoreComparatorFactoryConfig() != null) {
            deciderScoreComparatorFactoryConfig.inherit(inheritedConfig.getDeciderScoreComparatorFactoryConfig());
        }
        if (selectorConfig == null) {
            selectorConfig = inheritedConfig.getSelectorConfig();
        } else if (inheritedConfig.getSelectorConfig() != null) {
            selectorConfig.inherit(inheritedConfig.getSelectorConfig());
        }
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
