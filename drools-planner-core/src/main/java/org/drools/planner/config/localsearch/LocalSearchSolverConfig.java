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
import org.drools.planner.config.AbstractSolverConfig;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.localsearch.decider.acceptor.AcceptorConfig;
import org.drools.planner.config.localsearch.decider.deciderscorecomparator.DeciderScoreComparatorFactoryConfig;
import org.drools.planner.config.localsearch.decider.forager.ForagerConfig;
import org.drools.planner.config.localsearch.decider.selector.SelectorConfig;
import org.drools.planner.config.localsearch.termination.TerminationConfig;
import org.drools.planner.core.localsearch.DefaultLocalSearchSolver;
import org.drools.planner.core.localsearch.LocalSearchSolver;
import org.drools.planner.core.localsearch.decider.Decider;
import org.drools.planner.core.localsearch.decider.DefaultDecider;

/**
 * @author Geoffrey De Smet
 */
@XStreamAlias("localSearchSolver")
public class LocalSearchSolverConfig extends AbstractSolverConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XStreamAlias("termination")
    // TODO this new TerminationConfig is pointless due to xstream
    // TODO but maybe we should be able to use the config API directly too
    private TerminationConfig terminationConfig = new TerminationConfig();

    @XStreamAlias("deciderScoreComparatorFactory")
    private DeciderScoreComparatorFactoryConfig deciderScoreComparatorFactoryConfig
            = new DeciderScoreComparatorFactoryConfig();
    @XStreamAlias("selector")
    private SelectorConfig selectorConfig = new SelectorConfig();
    @XStreamAlias("acceptor")
    private AcceptorConfig acceptorConfig = new AcceptorConfig();
    @XStreamAlias("forager")
    private ForagerConfig foragerConfig = new ForagerConfig();

    public TerminationConfig getTerminationConfig() {
        return terminationConfig;
    }

    public void setTerminationConfig(TerminationConfig terminationConfig) {
        this.terminationConfig = terminationConfig;
    }

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

    public LocalSearchSolver buildSolver() {
        DefaultLocalSearchSolver localSearchSolver = new DefaultLocalSearchSolver();
        configureAbstractSolver(localSearchSolver);
        localSearchSolver.setTermination(terminationConfig.buildTermination(localSearchSolver.getScoreDefinition()));
        localSearchSolver.setDecider(buildDecider());
        if (environmentMode == EnvironmentMode.DEBUG || environmentMode == EnvironmentMode.TRACE) {
            localSearchSolver.setAssertStepScoreIsUncorrupted(true);
        }
        return localSearchSolver;
    }

    private Decider buildDecider() {
        DefaultDecider decider = new DefaultDecider();
        decider.setDeciderScoreComparator(deciderScoreComparatorFactoryConfig.buildDeciderScoreComparatorFactory());
        decider.setSelector(selectorConfig.buildSelector());
        decider.setAcceptor(acceptorConfig.buildAcceptor());
        decider.setForager(foragerConfig.buildForager());
        if ( environmentMode == EnvironmentMode.TRACE) {
            decider.setAssertMoveScoreIsUncorrupted(true);
        }
        if (environmentMode == EnvironmentMode.DEBUG || environmentMode == EnvironmentMode.TRACE) {
            decider.setAssertUndoMoveIsUncorrupted(true);
        }
        return decider;
    }

    public void inherit(LocalSearchSolverConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (terminationConfig == null) {
            terminationConfig = inheritedConfig.getTerminationConfig();
        } else if (inheritedConfig.getTerminationConfig() != null) {
            terminationConfig.inherit(inheritedConfig.getTerminationConfig());
        }
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
