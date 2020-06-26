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

package org.optaplanner.core.config.heuristic.selector.common.nearby;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import javax.xml.bind.annotation.XmlElement;

import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.BetaDistributionNearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.BlockDistributionNearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.LinearDistributionNearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.ParabolicDistributionNearbyRandom;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.heuristic.selector.entity.nearby.NearEntityNearbyEntitySelector;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.heuristic.selector.value.nearby.NearEntityNearbyValueSelector;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("nearbySelection")
public class NearbySelectionConfig extends SelectorConfig<NearbySelectionConfig> {

    @XmlElement(name = "originEntitySelector")
    @XStreamAlias("originEntitySelector")
    protected EntitySelectorConfig originEntitySelectorConfig = null;
    protected Class<? extends NearbyDistanceMeter> nearbyDistanceMeterClass = null;

    protected NearbySelectionDistributionType nearbySelectionDistributionType = null;

    protected Integer blockDistributionSizeMinimum = null;
    protected Integer blockDistributionSizeMaximum = null;
    protected Double blockDistributionSizeRatio = null;
    protected Double blockDistributionUniformDistributionProbability = null;

    protected Integer linearDistributionSizeMaximum = null;

    protected Integer parabolicDistributionSizeMaximum = null;

    protected Double betaDistributionAlpha = null;
    protected Double betaDistributionBeta = null;

    public EntitySelectorConfig getOriginEntitySelectorConfig() {
        return originEntitySelectorConfig;
    }

    public void setOriginEntitySelectorConfig(EntitySelectorConfig originEntitySelectorConfig) {
        this.originEntitySelectorConfig = originEntitySelectorConfig;
    }

    public Class<? extends NearbyDistanceMeter> getNearbyDistanceMeterClass() {
        return nearbyDistanceMeterClass;
    }

    public void setNearbyDistanceMeterClass(Class<? extends NearbyDistanceMeter> nearbyDistanceMeterClass) {
        this.nearbyDistanceMeterClass = nearbyDistanceMeterClass;
    }

    public NearbySelectionDistributionType getNearbySelectionDistributionType() {
        return nearbySelectionDistributionType;
    }

    public void setNearbySelectionDistributionType(NearbySelectionDistributionType nearbySelectionDistributionType) {
        this.nearbySelectionDistributionType = nearbySelectionDistributionType;
    }

    public Integer getBlockDistributionSizeMinimum() {
        return blockDistributionSizeMinimum;
    }

    public void setBlockDistributionSizeMinimum(Integer blockDistributionSizeMinimum) {
        this.blockDistributionSizeMinimum = blockDistributionSizeMinimum;
    }

    public Integer getBlockDistributionSizeMaximum() {
        return blockDistributionSizeMaximum;
    }

    public void setBlockDistributionSizeMaximum(Integer blockDistributionSizeMaximum) {
        this.blockDistributionSizeMaximum = blockDistributionSizeMaximum;
    }

    public Double getBlockDistributionSizeRatio() {
        return blockDistributionSizeRatio;
    }

    public void setBlockDistributionSizeRatio(Double blockDistributionSizeRatio) {
        this.blockDistributionSizeRatio = blockDistributionSizeRatio;
    }

    public Double getBlockDistributionUniformDistributionProbability() {
        return blockDistributionUniformDistributionProbability;
    }

    public void setBlockDistributionUniformDistributionProbability(Double blockDistributionUniformDistributionProbability) {
        this.blockDistributionUniformDistributionProbability = blockDistributionUniformDistributionProbability;
    }

    public Integer getLinearDistributionSizeMaximum() {
        return linearDistributionSizeMaximum;
    }

    public void setLinearDistributionSizeMaximum(Integer linearDistributionSizeMaximum) {
        this.linearDistributionSizeMaximum = linearDistributionSizeMaximum;
    }

    public Integer getParabolicDistributionSizeMaximum() {
        return parabolicDistributionSizeMaximum;
    }

    public void setParabolicDistributionSizeMaximum(Integer parabolicDistributionSizeMaximum) {
        this.parabolicDistributionSizeMaximum = parabolicDistributionSizeMaximum;
    }

    public Double getBetaDistributionAlpha() {
        return betaDistributionAlpha;
    }

    public void setBetaDistributionAlpha(Double betaDistributionAlpha) {
        this.betaDistributionAlpha = betaDistributionAlpha;
    }

    public Double getBetaDistributionBeta() {
        return betaDistributionBeta;
    }

    public void setBetaDistributionBeta(Double betaDistributionBeta) {
        this.betaDistributionBeta = betaDistributionBeta;
    }

    public void validateNearby(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder) {
        if (originEntitySelectorConfig == null) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") is nearby selection"
                    + " but lacks an originEntitySelectorConfig (" + originEntitySelectorConfig + ").");
        }
        if (originEntitySelectorConfig.getMimicSelectorRef() == null) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has an originEntitySelectorConfig (" + originEntitySelectorConfig
                    + ") which has no MimicSelectorRef (" + originEntitySelectorConfig.getMimicSelectorRef() + "). "
                    + "A nearby's original entity should always be the same as an entity selected earlier in the move.");
        }
        if (nearbyDistanceMeterClass == null) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") is nearby selection"
                    + " but lacks a nearbyDistanceMeterClass (" + nearbyDistanceMeterClass + ").");
        }
        if (resolvedSelectionOrder != SelectionOrder.ORIGINAL && resolvedSelectionOrder != SelectionOrder.RANDOM) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") with nearbyOriginEntitySelector (" + originEntitySelectorConfig
                    + ") and nearbyDistanceMeterClass (" + nearbyDistanceMeterClass
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.ORIGINAL + " or " + SelectionOrder.RANDOM + ".");
        }
        if (resolvedCacheType.isCached()) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") with nearbyOriginEntitySelector (" + originEntitySelectorConfig
                    + ") and nearbyDistanceMeterClass (" + nearbyDistanceMeterClass
                    + ") has a resolvedCacheType (" + resolvedCacheType
                    + ") that is cached.");
        }
    }

    public EntitySelector applyNearbyEntitySelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, EntitySelector entitySelector) {
        boolean randomSelection = resolvedSelectionOrder.toRandomSelectionBoolean();
        EntitySelector originEntitySelector = originEntitySelectorConfig.buildEntitySelector(
                configPolicy,
                minimumCacheType, resolvedSelectionOrder);
        NearbyDistanceMeter nearbyDistanceMeter = ConfigUtils.newInstance(this,
                "nearbyDistanceMeterClass", nearbyDistanceMeterClass);
        // TODO Check nearbyDistanceMeterClass.getGenericInterfaces() to confirm generic type S is an entityClass
        NearbyRandom nearbyRandom = buildNearbyRandom(randomSelection);
        return new NearEntityNearbyEntitySelector(entitySelector, originEntitySelector,
                nearbyDistanceMeter, nearbyRandom, randomSelection);
    }

    public ValueSelector applyNearbyValueSelector(HeuristicConfigPolicy configPolicy,
            SelectionCacheType minimumCacheType, SelectionCacheType resolvedCacheType,
            SelectionOrder resolvedSelectionOrder, ValueSelector valueSelector) {
        boolean randomSelection = resolvedSelectionOrder.toRandomSelectionBoolean();
        EntitySelector originEntitySelector = originEntitySelectorConfig.buildEntitySelector(
                configPolicy, minimumCacheType, resolvedSelectionOrder);
        NearbyDistanceMeter nearbyDistanceMeter = ConfigUtils.newInstance(this,
                "nearbyDistanceMeterClass", nearbyDistanceMeterClass);
        // TODO Check nearbyDistanceMeterClass.getGenericInterfaces() to confirm generic type S is an entityClass
        NearbyRandom nearbyRandom = buildNearbyRandom(randomSelection);
        return new NearEntityNearbyValueSelector(valueSelector, originEntitySelector,
                nearbyDistanceMeter, nearbyRandom, randomSelection);
    }

    protected NearbyRandom buildNearbyRandom(boolean randomSelection) {
        boolean blockDistributionEnabled = nearbySelectionDistributionType == NearbySelectionDistributionType.BLOCK_DISTRIBUTION
                || blockDistributionSizeMinimum != null
                || blockDistributionSizeMaximum != null
                || blockDistributionSizeRatio != null
                || blockDistributionUniformDistributionProbability != null;
        boolean linearDistributionEnabled =
                nearbySelectionDistributionType == NearbySelectionDistributionType.LINEAR_DISTRIBUTION
                        || linearDistributionSizeMaximum != null;
        boolean parabolicDistributionEnabled =
                nearbySelectionDistributionType == NearbySelectionDistributionType.PARABOLIC_DISTRIBUTION
                        || parabolicDistributionSizeMaximum != null;
        boolean betaDistributionEnabled = nearbySelectionDistributionType == NearbySelectionDistributionType.BETA_DISTRIBUTION
                || betaDistributionAlpha != null
                || betaDistributionBeta != null;
        if (!randomSelection) {
            if (blockDistributionEnabled || linearDistributionEnabled || parabolicDistributionEnabled
                    || betaDistributionEnabled) {
                throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                        + ") with randomSelection (" + randomSelection
                        + ") has distribution parameters.");
            }
            return null;
        }
        if (blockDistributionEnabled && linearDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has both blockDistribution and linearDistribution parameters.");
        }
        if (blockDistributionEnabled && parabolicDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has both blockDistribution and parabolicDistribution parameters.");
        }
        if (blockDistributionEnabled && betaDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has both blockDistribution and betaDistribution parameters.");
        }
        if (linearDistributionEnabled && parabolicDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has both linearDistribution and parabolicDistribution parameters.");
        }
        if (linearDistributionEnabled && betaDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has both linearDistribution and betaDistribution parameters.");
        }
        if (parabolicDistributionEnabled && betaDistributionEnabled) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has both parabolicDistribution and betaDistribution parameters.");
        }
        if (blockDistributionEnabled) {
            int sizeMinimum = defaultIfNull(blockDistributionSizeMinimum, 1);
            int sizeMaximum = defaultIfNull(blockDistributionSizeMaximum, Integer.MAX_VALUE);
            double sizeRatio = defaultIfNull(blockDistributionSizeRatio, 1.0);
            double uniformDistributionProbability = defaultIfNull(blockDistributionUniformDistributionProbability, 0.0);
            return new BlockDistributionNearbyRandom(sizeMinimum, sizeMaximum, sizeRatio, uniformDistributionProbability);
        } else if (linearDistributionEnabled) {
            int sizeMaximum = defaultIfNull(linearDistributionSizeMaximum, Integer.MAX_VALUE);
            return new LinearDistributionNearbyRandom(sizeMaximum);
        } else if (parabolicDistributionEnabled) {
            int sizeMaximum = defaultIfNull(parabolicDistributionSizeMaximum, Integer.MAX_VALUE);
            return new ParabolicDistributionNearbyRandom(sizeMaximum);
        } else if (betaDistributionEnabled) {
            double alpha = defaultIfNull(betaDistributionAlpha, 1.0);
            double beta = defaultIfNull(betaDistributionBeta, 5.0);
            return new BetaDistributionNearbyRandom(alpha, beta);
        } else {
            return new LinearDistributionNearbyRandom(Integer.MAX_VALUE);
        }
    }

    @Override
    public NearbySelectionConfig inherit(NearbySelectionConfig inheritedConfig) {
        originEntitySelectorConfig = ConfigUtils.inheritConfig(originEntitySelectorConfig,
                inheritedConfig.getOriginEntitySelectorConfig());
        nearbyDistanceMeterClass = ConfigUtils.inheritOverwritableProperty(nearbyDistanceMeterClass,
                inheritedConfig.getNearbyDistanceMeterClass());
        nearbySelectionDistributionType = ConfigUtils.inheritOverwritableProperty(nearbySelectionDistributionType,
                inheritedConfig.getNearbySelectionDistributionType());
        blockDistributionSizeMinimum = ConfigUtils.inheritOverwritableProperty(blockDistributionSizeMinimum,
                inheritedConfig.getBlockDistributionSizeMinimum());
        blockDistributionSizeMaximum = ConfigUtils.inheritOverwritableProperty(blockDistributionSizeMaximum,
                inheritedConfig.getBlockDistributionSizeMaximum());
        blockDistributionSizeRatio = ConfigUtils.inheritOverwritableProperty(blockDistributionSizeRatio,
                inheritedConfig.getBlockDistributionSizeRatio());
        blockDistributionUniformDistributionProbability = ConfigUtils.inheritOverwritableProperty(
                blockDistributionUniformDistributionProbability,
                inheritedConfig.getBlockDistributionUniformDistributionProbability());
        linearDistributionSizeMaximum = ConfigUtils.inheritOverwritableProperty(linearDistributionSizeMaximum,
                inheritedConfig.getLinearDistributionSizeMaximum());
        parabolicDistributionSizeMaximum = ConfigUtils.inheritOverwritableProperty(parabolicDistributionSizeMaximum,
                inheritedConfig.getParabolicDistributionSizeMaximum());
        betaDistributionAlpha = ConfigUtils.inheritOverwritableProperty(betaDistributionAlpha,
                inheritedConfig.getBetaDistributionAlpha());
        betaDistributionBeta = ConfigUtils.inheritOverwritableProperty(betaDistributionBeta,
                inheritedConfig.getBetaDistributionBeta());
        return this;
    }

    @Override
    public NearbySelectionConfig copyConfig() {
        return new NearbySelectionConfig().inherit(this);
    }

}
