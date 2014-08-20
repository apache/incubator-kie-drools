/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.config.exhaustivesearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.exhaustivesearch.DefaultExhaustiveSearchPhase;
import org.optaplanner.core.impl.exhaustivesearch.ExhaustiveSearchPhase;
import org.optaplanner.core.impl.exhaustivesearch.decider.ExhaustiveSearchDecider;
import org.optaplanner.core.impl.exhaustivesearch.node.bounder.TrendBasedScoreBounder;
import org.optaplanner.core.impl.exhaustivesearch.node.bounder.ScoreBounder;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.mimic.ManualEntityMimicRecorder;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;

@XStreamAlias("exhaustiveSearch")
public class ExhaustiveSearchPhaseConfig extends PhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected ExhaustiveSearchType exhaustiveSearchType = null;
    protected NodeExplorationType nodeExplorationType = null;
    protected EntitySorterManner entitySorterManner = null;
    protected ValueSorterManner valueSorterManner = null;

    @XStreamAlias("entitySelector")
    protected EntitySelectorConfig entitySelectorConfig = null;
    @XStreamAlias("moveSelector")
    protected MoveSelectorConfig moveSelectorConfig = null;

    public ExhaustiveSearchType getExhaustiveSearchType() {
        return exhaustiveSearchType;
    }

    public void setExhaustiveSearchType(ExhaustiveSearchType exhaustiveSearchType) {
        this.exhaustiveSearchType = exhaustiveSearchType;
    }

    public NodeExplorationType getNodeExplorationType() {
        return nodeExplorationType;
    }

    public void setNodeExplorationType(NodeExplorationType nodeExplorationType) {
        this.nodeExplorationType = nodeExplorationType;
    }

    public EntitySorterManner getEntitySorterManner() {
        return entitySorterManner;
    }

    public void setEntitySorterManner(EntitySorterManner entitySorterManner) {
        this.entitySorterManner = entitySorterManner;
    }

    public ValueSorterManner getValueSorterManner() {
        return valueSorterManner;
    }

    public void setValueSorterManner(ValueSorterManner valueSorterManner) {
        this.valueSorterManner = valueSorterManner;
    }

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public MoveSelectorConfig getMoveSelectorConfig() {
        return moveSelectorConfig;
    }

    public void setMoveSelectorConfig(MoveSelectorConfig moveSelectorConfig) {
        this.moveSelectorConfig = moveSelectorConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public ExhaustiveSearchPhase buildPhase(int phaseIndex, HeuristicConfigPolicy solverConfigPolicy,
            BestSolutionRecaller bestSolutionRecaller, Termination solverTermination) {
        HeuristicConfigPolicy phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        phaseConfigPolicy.setInitializedChainedValueFilterEnabled(true);
        ExhaustiveSearchType exhaustiveSearchType_ = exhaustiveSearchType == null
                ? ExhaustiveSearchType.BRANCH_AND_BOUND : exhaustiveSearchType;
        phaseConfigPolicy.setEntitySorterManner(entitySorterManner != null ? entitySorterManner
                : exhaustiveSearchType_.getDefaultEntitySorterManner());
        phaseConfigPolicy.setValueSorterManner(valueSorterManner != null ? valueSorterManner
                : exhaustiveSearchType_.getDefaultValueSorterManner());
        DefaultExhaustiveSearchPhase phase = new DefaultExhaustiveSearchPhase();
        configurePhase(phase, phaseIndex, phaseConfigPolicy, bestSolutionRecaller, solverTermination);
        boolean scoreBounderEnabled = exhaustiveSearchType_.isScoreBounderEnabled();
        NodeExplorationType nodeExplorationType_ = nodeExplorationType != null ? nodeExplorationType
                : (exhaustiveSearchType_ == ExhaustiveSearchType.BRUTE_FORCE
                ? NodeExplorationType.ORIGINAL_ORDER : NodeExplorationType.DEPTH_FIRST);
        phase.setNodeComparator(nodeExplorationType_.buildNodeComparator(scoreBounderEnabled));
        EntitySelectorConfig entitySelectorConfig_ = buildEntitySelectorConfig(phaseConfigPolicy);
        EntitySelector entitySelector = entitySelectorConfig_.buildEntitySelector(phaseConfigPolicy,
                SelectionCacheType.PHASE, SelectionOrder.ORIGINAL);
        phase.setEntitySelector(entitySelector);
        phase.setDecider(buildDecider(phaseConfigPolicy, entitySelector, bestSolutionRecaller, phase.getTermination(),
                scoreBounderEnabled));
        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            phase.setAssertWorkingSolutionScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            phase.setAssertExpectedWorkingSolutionScore(true);
        }
        return phase;
    }

    private EntitySelectorConfig buildEntitySelectorConfig(HeuristicConfigPolicy configPolicy) {
        EntitySelectorConfig entitySelectorConfig_;
        if (entitySelectorConfig == null) {
            entitySelectorConfig_ = new EntitySelectorConfig();
            EntityDescriptor entityDescriptor = deduceEntityDescriptor(configPolicy.getSolutionDescriptor());
            entitySelectorConfig_.setEntityClass(entityDescriptor.getEntityClass());
            if (configPolicy.getEntitySorterManner().hasSorter(entityDescriptor)) {
                entitySelectorConfig_.setCacheType(SelectionCacheType.PHASE);
                entitySelectorConfig_.setSelectionOrder(SelectionOrder.SORTED);
                entitySelectorConfig_.setSorterManner(configPolicy.getEntitySorterManner());
            }
        } else {
            entitySelectorConfig_ = entitySelectorConfig;
        }
        if (entitySelectorConfig_.getCacheType() != null
                && entitySelectorConfig_.getCacheType().compareTo(SelectionCacheType.PHASE) < 0) {
            throw new IllegalArgumentException("The phaseConfig (" + this
                    + ") cannot have an entitySelectorConfig ("  + entitySelectorConfig_
                    + ") with a cacheType (" + entitySelectorConfig_.getCacheType()
                    + ") lower than " + SelectionCacheType.PHASE + ".");
        }
        return entitySelectorConfig_;
    }

    protected EntityDescriptor deduceEntityDescriptor(SolutionDescriptor solutionDescriptor) {
        Collection<EntityDescriptor> entityDescriptors = solutionDescriptor.getGenuineEntityDescriptors();
        if (entityDescriptors.size() != 1) {
            throw new IllegalArgumentException("The phaseConfig (" + this
                    + ") has no entitySelector configured"
                    + " and because there are multiple in the entityClassSet (" + solutionDescriptor.getEntityClassSet()
                    + "), it can not be deducted automatically.");
        }
        return entityDescriptors.iterator().next();
    }

    private ExhaustiveSearchDecider buildDecider(HeuristicConfigPolicy configPolicy,
            EntitySelector sourceEntitySelector,
            BestSolutionRecaller bestSolutionRecaller, Termination termination,
            boolean scoreBounderEnabled) {
        ManualEntityMimicRecorder manualEntityMimicRecorder = new ManualEntityMimicRecorder(sourceEntitySelector);
        String mimicSelectorId = sourceEntitySelector.getEntityDescriptor().getEntityClass().getName(); // TODO mimicSelectorId must be a field
        configPolicy.addEntityMimicRecorder(mimicSelectorId, manualEntityMimicRecorder);
        MoveSelectorConfig moveSelectorConfig_ = buildMoveSelectorConfig(configPolicy,
                sourceEntitySelector, mimicSelectorId);
        MoveSelector moveSelector = moveSelectorConfig_.buildMoveSelector(configPolicy,
                SelectionCacheType.JUST_IN_TIME, SelectionOrder.ORIGINAL);
        ScoreBounder scoreBounder = scoreBounderEnabled
                ? new TrendBasedScoreBounder(configPolicy.getScoreDirectorFactory()) : null;
        ExhaustiveSearchDecider decider = new ExhaustiveSearchDecider(bestSolutionRecaller, termination,
                manualEntityMimicRecorder, moveSelector, scoreBounderEnabled, scoreBounder);
        EnvironmentMode environmentMode = configPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            decider.setAssertMoveScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            decider.setAssertExpectedUndoMoveScore(true);
        }
        return decider;
    }

    private MoveSelectorConfig buildMoveSelectorConfig(HeuristicConfigPolicy configPolicy,
            EntitySelector entitySelector, String mimicSelectorId) {
        MoveSelectorConfig moveSelectorConfig_;
        if (moveSelectorConfig == null) {
            EntityDescriptor entityDescriptor = entitySelector.getEntityDescriptor();
            // Keep in sync with org.optaplanner.core.impl.exhaustivesearch.DefaultExhaustiveSearchPhase.fillLayerList()
            Collection<GenuineVariableDescriptor> variableDescriptors = entityDescriptor.getGenuineVariableDescriptors();
            List<MoveSelectorConfig> subMoveSelectorConfigList = new ArrayList<MoveSelectorConfig>(
                    variableDescriptors.size());
            for (GenuineVariableDescriptor variableDescriptor : variableDescriptors) {
                ChangeMoveSelectorConfig changeMoveSelectorConfig = new ChangeMoveSelectorConfig();
                EntitySelectorConfig changeEntitySelectorConfig = new EntitySelectorConfig();
                changeEntitySelectorConfig.setMimicSelectorRef(mimicSelectorId);
                changeMoveSelectorConfig.setEntitySelectorConfig(changeEntitySelectorConfig);
                ValueSelectorConfig changeValueSelectorConfig = new ValueSelectorConfig();
                changeValueSelectorConfig.setVariableName(variableDescriptor.getVariableName());
                if (configPolicy.getValueSorterManner().hasSorter(variableDescriptor)) {
                    if (variableDescriptor.isValueRangeEntityIndependent()) {
                        changeValueSelectorConfig.setCacheType(SelectionCacheType.PHASE);
                    } else {
                        changeValueSelectorConfig.setCacheType(SelectionCacheType.STEP);
                    }
                    changeValueSelectorConfig.setSelectionOrder(SelectionOrder.SORTED);
                    changeValueSelectorConfig.setSorterManner(configPolicy.getValueSorterManner());
                }
                changeMoveSelectorConfig.setValueSelectorConfig(changeValueSelectorConfig);
                subMoveSelectorConfigList.add(changeMoveSelectorConfig);
            }
            if (subMoveSelectorConfigList.size() > 1) {
                moveSelectorConfig_ = new CartesianProductMoveSelectorConfig(subMoveSelectorConfigList);
            } else {
                moveSelectorConfig_ = subMoveSelectorConfigList.get(0);
            }
        } else {
            moveSelectorConfig_ = moveSelectorConfig;
        }
        return moveSelectorConfig_;
    }

    public void inherit(ExhaustiveSearchPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        exhaustiveSearchType = ConfigUtils.inheritOverwritableProperty(exhaustiveSearchType,
                inheritedConfig.getExhaustiveSearchType());
        nodeExplorationType = ConfigUtils.inheritOverwritableProperty(nodeExplorationType,
                inheritedConfig.getNodeExplorationType());
        entitySorterManner = ConfigUtils.inheritOverwritableProperty(entitySorterManner,
                inheritedConfig.getEntitySorterManner());
        valueSorterManner = ConfigUtils.inheritOverwritableProperty(valueSorterManner,
                inheritedConfig.getValueSorterManner());
        if (entitySelectorConfig == null) {
            entitySelectorConfig = inheritedConfig.getEntitySelectorConfig();
        } else if (inheritedConfig.getEntitySelectorConfig() != null) {
            entitySelectorConfig.inherit(inheritedConfig.getEntitySelectorConfig());
        }
        if (moveSelectorConfig == null) {
            moveSelectorConfig = inheritedConfig.getMoveSelectorConfig();
        } else if (inheritedConfig.getMoveSelectorConfig() != null) {
            moveSelectorConfig.inherit(inheritedConfig.getMoveSelectorConfig());
        }
    }

}
