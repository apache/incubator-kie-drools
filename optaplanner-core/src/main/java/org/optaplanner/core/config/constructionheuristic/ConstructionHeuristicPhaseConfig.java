/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.constructionheuristic;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicForagerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.EntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.PooledEntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedEntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedValuePlacerConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySorterManner;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.CartesianProductMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveIteratorFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.factory.MoveListFactoryConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.PillarSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.KOptMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.TailChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.constructionheuristic.ConstructionHeuristicPhase;
import org.optaplanner.core.impl.constructionheuristic.DefaultConstructionHeuristicPhase;
import org.optaplanner.core.impl.constructionheuristic.decider.ConstructionHeuristicDecider;
import org.optaplanner.core.impl.constructionheuristic.decider.MultiThreadedConstructionHeuristicDecider;
import org.optaplanner.core.impl.constructionheuristic.decider.forager.ConstructionHeuristicForager;
import org.optaplanner.core.impl.constructionheuristic.placer.EntityPlacer;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.solver.thread.ChildThreadType;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("constructionHeuristic")
public class ConstructionHeuristicPhaseConfig extends PhaseConfig<ConstructionHeuristicPhaseConfig> {

    public static final String XML_ELEMENT_NAME = "constructionHeuristic";

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected ConstructionHeuristicType constructionHeuristicType = null;
    protected EntitySorterManner entitySorterManner = null;
    protected ValueSorterManner valueSorterManner = null;

    // TODO This is a List due to XStream limitations. With JAXB it could be just a EntityPlacerConfig instead.
    @XmlElements({
            @XmlElement(name = "queuedEntityPlacer", type = QueuedEntityPlacerConfig.class),
            @XmlElement(name = "queuedValuePlacer", type = QueuedValuePlacerConfig.class),
            @XmlElement(name = "pooledEntityPlacer", type = PooledEntityPlacerConfig.class)
    })
    @XStreamImplicit
    protected List<EntityPlacerConfig> entityPlacerConfigList = null;

    /** Simpler alternative for {@link #entityPlacerConfigList}. */
    @XmlElements({
            @XmlElement(name = CartesianProductMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = CartesianProductMoveSelectorConfig.class),
            @XmlElement(name = ChangeMoveSelectorConfig.XML_ELEMENT_NAME, type = ChangeMoveSelectorConfig.class),
            @XmlElement(name = KOptMoveSelectorConfig.XML_ELEMENT_NAME, type = KOptMoveSelectorConfig.class),
            @XmlElement(name = MoveIteratorFactoryConfig.XML_ELEMENT_NAME, type = MoveIteratorFactoryConfig.class),
            @XmlElement(name = MoveListFactoryConfig.XML_ELEMENT_NAME, type = MoveListFactoryConfig.class),
            @XmlElement(name = PillarChangeMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = PillarChangeMoveSelectorConfig.class),
            @XmlElement(name = PillarSwapMoveSelectorConfig.XML_ELEMENT_NAME, type = PillarSwapMoveSelectorConfig.class),
            @XmlElement(name = SubChainChangeMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = SubChainChangeMoveSelectorConfig.class),
            @XmlElement(name = SubChainSwapMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = SubChainSwapMoveSelectorConfig.class),
            @XmlElement(name = SwapMoveSelectorConfig.XML_ELEMENT_NAME, type = SwapMoveSelectorConfig.class),
            @XmlElement(name = TailChainSwapMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = TailChainSwapMoveSelectorConfig.class),
            @XmlElement(name = UnionMoveSelectorConfig.XML_ELEMENT_NAME, type = UnionMoveSelectorConfig.class)
    })
    @XStreamImplicit()
    protected List<MoveSelectorConfig> moveSelectorConfigList = null;

    @XmlElement(name = "forager")
    @XStreamAlias("forager")
    protected ConstructionHeuristicForagerConfig foragerConfig = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public ConstructionHeuristicType getConstructionHeuristicType() {
        return constructionHeuristicType;
    }

    public void setConstructionHeuristicType(ConstructionHeuristicType constructionHeuristicType) {
        this.constructionHeuristicType = constructionHeuristicType;
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

    public EntityPlacerConfig getEntityPlacerConfig() {
        return entityPlacerConfigList == null ? null : entityPlacerConfigList.get(0);
    }

    public void setEntityPlacerConfig(EntityPlacerConfig entityPlacerConfig) {
        this.entityPlacerConfigList = entityPlacerConfig == null ? null : Collections.singletonList(entityPlacerConfig);
    }

    public List<MoveSelectorConfig> getMoveSelectorConfigList() {
        return moveSelectorConfigList;
    }

    public void setMoveSelectorConfigList(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
    }

    public ConstructionHeuristicForagerConfig getForagerConfig() {
        return foragerConfig;
    }

    public void setForagerConfig(ConstructionHeuristicForagerConfig foragerConfig) {
        this.foragerConfig = foragerConfig;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public ConstructionHeuristicPhaseConfig withConstructionHeuristicType(ConstructionHeuristicType constructionHeuristicType) {
        this.constructionHeuristicType = constructionHeuristicType;
        return this;
    }

    public ConstructionHeuristicPhaseConfig withEntitySorterManner(EntitySorterManner entitySorterManner) {
        this.entitySorterManner = entitySorterManner;
        return this;
    }

    public ConstructionHeuristicPhaseConfig withValueSorterManner(ValueSorterManner valueSorterManner) {
        this.valueSorterManner = valueSorterManner;
        return this;
    }

    public ConstructionHeuristicPhaseConfig withEntityPlacerConfig(EntityPlacerConfig entityPlacerConfig) {
        this.entityPlacerConfigList = entityPlacerConfig == null ? null : Collections.singletonList(entityPlacerConfig);
        return this;
    }

    public ConstructionHeuristicPhaseConfig withMoveSelectorConfigList(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
        return this;
    }

    public ConstructionHeuristicPhaseConfig withForagerConfig(ConstructionHeuristicForagerConfig foragerConfig) {
        this.foragerConfig = foragerConfig;
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public ConstructionHeuristicPhase buildPhase(int phaseIndex, HeuristicConfigPolicy solverConfigPolicy,
            BestSolutionRecaller bestSolutionRecaller, Termination solverTermination) {
        HeuristicConfigPolicy phaseConfigPolicy = solverConfigPolicy.createFilteredPhaseConfigPolicy();
        DefaultConstructionHeuristicPhase phase = new DefaultConstructionHeuristicPhase(
                phaseIndex, solverConfigPolicy.getLogIndentation(), bestSolutionRecaller,
                buildPhaseTermination(phaseConfigPolicy, solverTermination));
        phase.setDecider(buildDecider(phaseConfigPolicy, phase.getTermination()));
        ConstructionHeuristicType constructionHeuristicType_ = defaultIfNull(
                constructionHeuristicType, ConstructionHeuristicType.ALLOCATE_ENTITY_FROM_QUEUE);
        phaseConfigPolicy.setEntitySorterManner(entitySorterManner != null ? entitySorterManner
                : constructionHeuristicType_.getDefaultEntitySorterManner());
        phaseConfigPolicy.setValueSorterManner(valueSorterManner != null ? valueSorterManner
                : constructionHeuristicType_.getDefaultValueSorterManner());
        EntityPlacerConfig entityPlacerConfig;
        if (ConfigUtils.isEmptyCollection(entityPlacerConfigList)) {
            entityPlacerConfig = buildUnfoldedEntityPlacerConfig(phaseConfigPolicy, constructionHeuristicType_);
        } else if (entityPlacerConfigList.size() == 1) {
            entityPlacerConfig = entityPlacerConfigList.get(0);
            if (constructionHeuristicType != null) {
                throw new IllegalArgumentException("The constructionHeuristicType (" + constructionHeuristicType
                        + ") must not be configured if the entityPlacerConfig (" + entityPlacerConfig
                        + ") is explicitly configured.");
            }
            if (moveSelectorConfigList != null) {
                throw new IllegalArgumentException("The moveSelectorConfigList (" + moveSelectorConfigList
                        + ") can not be configured if the entityPlacerConfig (" + entityPlacerConfig
                        + ") is explicitly configured.");
            }
        } else {
            // TODO entityPlacerConfigList is only a List because of XStream limitations.
            throw new IllegalArgumentException("The entityPlacerConfigList (" + entityPlacerConfigList
                    + ") must be a singleton or empty. Use multiple " + ConstructionHeuristicPhaseConfig.class
                    + " elements to initialize multiple entity classes.");
        }
        EntityPlacer entityPlacer = entityPlacerConfig.buildEntityPlacer(phaseConfigPolicy);
        phase.setEntityPlacer(entityPlacer);
        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            phase.setAssertStepScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            phase.setAssertExpectedStepScore(true);
            phase.setAssertShadowVariablesAreNotStaleAfterStep(true);
        }
        return phase;
    }

    private ConstructionHeuristicDecider buildDecider(HeuristicConfigPolicy configPolicy, Termination termination) {
        ConstructionHeuristicForagerConfig foragerConfig_ = foragerConfig == null
                ? new ConstructionHeuristicForagerConfig()
                : foragerConfig;
        ConstructionHeuristicForager forager = foragerConfig_.buildForager(configPolicy);
        EnvironmentMode environmentMode = configPolicy.getEnvironmentMode();
        ConstructionHeuristicDecider decider;
        Integer moveThreadCount = configPolicy.getMoveThreadCount();
        if (moveThreadCount == null) {
            decider = new ConstructionHeuristicDecider(
                    configPolicy.getLogIndentation(), termination, forager);
        } else {
            Integer moveThreadBufferSize = configPolicy.getMoveThreadBufferSize();
            if (moveThreadBufferSize == null) {
                // TODO Verify this is a good default by more meticulous benchmarking on multiple machines and JDK's
                // If it's too low, move threads will need to wait on the buffer, which hurts performance
                // If it's too high, more moves are selected that aren't foraged
                moveThreadBufferSize = 10;
            }
            ThreadFactory threadFactory = configPolicy.buildThreadFactory(ChildThreadType.MOVE_THREAD);
            int selectedMoveBufferSize = moveThreadCount * moveThreadBufferSize;
            MultiThreadedConstructionHeuristicDecider multiThreadedDecider = new MultiThreadedConstructionHeuristicDecider(
                    configPolicy.getLogIndentation(), termination, forager,
                    threadFactory, moveThreadCount, selectedMoveBufferSize);
            if (environmentMode.isNonIntrusiveFullAsserted()) {
                multiThreadedDecider.setAssertStepScoreFromScratch(true);
            }
            if (environmentMode.isIntrusiveFastAsserted()) {
                multiThreadedDecider.setAssertExpectedStepScore(true);
                multiThreadedDecider.setAssertShadowVariablesAreNotStaleAfterStep(true);
            }
            decider = multiThreadedDecider;
        }
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            decider.setAssertMoveScoreFromScratch(true);
        }
        if (environmentMode.isIntrusiveFastAsserted()) {
            decider.setAssertExpectedUndoMoveScore(true);
        }
        return decider;
    }

    private EntityPlacerConfig buildUnfoldedEntityPlacerConfig(
            HeuristicConfigPolicy phaseConfigPolicy, ConstructionHeuristicType constructionHeuristicType) {
        switch (constructionHeuristicType) {
            case FIRST_FIT:
            case FIRST_FIT_DECREASING:
            case WEAKEST_FIT:
            case WEAKEST_FIT_DECREASING:
            case STRONGEST_FIT:
            case STRONGEST_FIT_DECREASING:
            case ALLOCATE_ENTITY_FROM_QUEUE:
                if (!ConfigUtils.isEmptyCollection(moveSelectorConfigList)) {
                    return QueuedEntityPlacerConfig.unfoldNew(phaseConfigPolicy, moveSelectorConfigList);
                }
                return new QueuedEntityPlacerConfig();
            case ALLOCATE_TO_VALUE_FROM_QUEUE:
                if (!ConfigUtils.isEmptyCollection(moveSelectorConfigList)) {
                    if (moveSelectorConfigList.size() != 1) {
                        throw new IllegalArgumentException("For the constructionHeuristicType ("
                                + constructionHeuristicType + "), the moveSelectorConfigList (" + moveSelectorConfigList
                                + ") must be a singleton. Use a single " + UnionMoveSelectorConfig.class.getSimpleName()
                                + " or " + CartesianProductMoveSelectorConfig.class.getSimpleName()
                                + " element to nest multiple MoveSelectors.");
                    }
                    return QueuedValuePlacerConfig.unfoldNew(phaseConfigPolicy, moveSelectorConfigList.get(0));
                }
                return new QueuedValuePlacerConfig();
            case CHEAPEST_INSERTION:
            case ALLOCATE_FROM_POOL:
                if (!ConfigUtils.isEmptyCollection(moveSelectorConfigList)) {
                    if (moveSelectorConfigList.size() != 1) {
                        throw new IllegalArgumentException("For the constructionHeuristicType ("
                                + constructionHeuristicType + "), the moveSelectorConfigList (" + moveSelectorConfigList
                                + ") must be a singleton. Use a single " + UnionMoveSelectorConfig.class.getSimpleName()
                                + " or " + CartesianProductMoveSelectorConfig.class.getSimpleName()
                                + " element to nest multiple MoveSelectors.");
                    }
                    return PooledEntityPlacerConfig.unfoldNew(phaseConfigPolicy, moveSelectorConfigList.get(0));
                }
                return new PooledEntityPlacerConfig();
            default:
                throw new IllegalStateException(
                        "The constructionHeuristicType (" + constructionHeuristicType + ") is not implemented.");
        }
    }

    @Override
    public ConstructionHeuristicPhaseConfig inherit(ConstructionHeuristicPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        constructionHeuristicType = ConfigUtils.inheritOverwritableProperty(constructionHeuristicType,
                inheritedConfig.getConstructionHeuristicType());
        entitySorterManner = ConfigUtils.inheritOverwritableProperty(entitySorterManner,
                inheritedConfig.getEntitySorterManner());
        valueSorterManner = ConfigUtils.inheritOverwritableProperty(valueSorterManner,
                inheritedConfig.getValueSorterManner());
        setEntityPlacerConfig(ConfigUtils.inheritOverwritableProperty(
                getEntityPlacerConfig(), inheritedConfig.getEntityPlacerConfig()));
        moveSelectorConfigList = ConfigUtils.inheritMergeableListConfig(
                moveSelectorConfigList, inheritedConfig.getMoveSelectorConfigList());
        foragerConfig = ConfigUtils.inheritConfig(foragerConfig, inheritedConfig.getForagerConfig());
        return this;
    }

    @Override
    public ConstructionHeuristicPhaseConfig copyConfig() {
        return new ConstructionHeuristicPhaseConfig().inherit(this);
    }

}
