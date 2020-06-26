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

package org.optaplanner.core.config.heuristic.selector.move.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
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
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.FixedSelectorProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.composite.UnionMoveSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("unionMoveSelector")
public class UnionMoveSelectorConfig extends MoveSelectorConfig<UnionMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "unionMoveSelector";

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
    private List<MoveSelectorConfig> moveSelectorConfigList = null;

    private Class<? extends SelectionProbabilityWeightFactory> selectorProbabilityWeightFactoryClass = null;

    public UnionMoveSelectorConfig() {
    }

    public UnionMoveSelectorConfig(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
    }

    public List<MoveSelectorConfig> getMoveSelectorConfigList() {
        return moveSelectorConfigList;
    }

    public void setMoveSelectorConfigList(List<MoveSelectorConfig> moveSelectorConfigList) {
        this.moveSelectorConfigList = moveSelectorConfigList;
    }

    public Class<? extends SelectionProbabilityWeightFactory> getSelectorProbabilityWeightFactoryClass() {
        return selectorProbabilityWeightFactoryClass;
    }

    public void setSelectorProbabilityWeightFactoryClass(
            Class<? extends SelectionProbabilityWeightFactory> selectorProbabilityWeightFactoryClass) {
        this.selectorProbabilityWeightFactoryClass = selectorProbabilityWeightFactoryClass;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public MoveSelector buildBaseMoveSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, boolean randomSelection) {
        List<MoveSelector> moveSelectorList = new ArrayList<>(moveSelectorConfigList.size());
        for (MoveSelectorConfig moveSelectorConfig : moveSelectorConfigList) {
            moveSelectorList.add(
                    moveSelectorConfig.buildMoveSelector(configPolicy,
                            minimumCacheType, SelectionOrder.fromRandomSelectionBoolean(randomSelection)));
        }

        SelectionProbabilityWeightFactory selectorProbabilityWeightFactory;
        if (selectorProbabilityWeightFactoryClass != null) {
            if (!randomSelection) {
                throw new IllegalArgumentException("The moveSelectorConfig (" + this
                        + ") with selectorProbabilityWeightFactoryClass (" + selectorProbabilityWeightFactoryClass
                        + ") has non-random randomSelection (" + randomSelection + ").");
            }
            selectorProbabilityWeightFactory = ConfigUtils.newInstance(this,
                    "selectorProbabilityWeightFactoryClass", selectorProbabilityWeightFactoryClass);
        } else if (randomSelection) {
            Map<MoveSelector, Double> fixedProbabilityWeightMap = new HashMap<>(
                    moveSelectorConfigList.size());
            for (int i = 0; i < moveSelectorConfigList.size(); i++) {
                MoveSelectorConfig moveSelectorConfig = moveSelectorConfigList.get(i);
                MoveSelector moveSelector = moveSelectorList.get(i);
                Double fixedProbabilityWeight = moveSelectorConfig.getFixedProbabilityWeight();
                if (fixedProbabilityWeight == null) {
                    // Default to equal probability for each move type => unequal probability for each move instance
                    fixedProbabilityWeight = 1.0;
                }
                fixedProbabilityWeightMap.put(moveSelector, fixedProbabilityWeight);
            }
            selectorProbabilityWeightFactory = new FixedSelectorProbabilityWeightFactory(fixedProbabilityWeightMap);
        } else {
            selectorProbabilityWeightFactory = null;
        }
        return new UnionMoveSelector(moveSelectorList, randomSelection,
                selectorProbabilityWeightFactory);
    }

    @Override
    public void extractLeafMoveSelectorConfigsIntoList(List<MoveSelectorConfig> leafMoveSelectorConfigList) {
        for (MoveSelectorConfig moveSelectorConfig : moveSelectorConfigList) {
            moveSelectorConfig.extractLeafMoveSelectorConfigsIntoList(leafMoveSelectorConfigList);
        }
    }

    @Override
    public UnionMoveSelectorConfig inherit(UnionMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        moveSelectorConfigList = ConfigUtils.inheritMergeableListConfig(
                moveSelectorConfigList, inheritedConfig.getMoveSelectorConfigList());
        selectorProbabilityWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                selectorProbabilityWeightFactoryClass, inheritedConfig.getSelectorProbabilityWeightFactoryClass());
        return this;
    }

    @Override
    public UnionMoveSelectorConfig copyConfig() {
        return new UnionMoveSelectorConfig().inherit(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + moveSelectorConfigList + ")";
    }

}
