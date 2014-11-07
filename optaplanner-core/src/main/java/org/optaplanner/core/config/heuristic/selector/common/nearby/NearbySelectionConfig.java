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

package org.optaplanner.core.config.heuristic.selector.common.nearby;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
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

@XStreamAlias("nearbySelection")
public class NearbySelectionConfig extends SelectorConfig {

    @XStreamAlias("originEntitySelector")
    protected EntitySelectorConfig originEntitySelectorConfig = null;
    protected Class<? extends NearbyDistanceMeter> nearbyDistanceMeterClass = null;

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
                    + " but lacks a nearbyOriginEntitySelector (" + originEntitySelectorConfig + ").");
        }
        if (nearbyDistanceMeterClass == null) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") is nearby selection"
                    + " but lacks a nearbyDistanceMeterClass (" + nearbyDistanceMeterClass + ").");
        }
        if (resolvedSelectionOrder != SelectionOrder.ORIGINAL && resolvedSelectionOrder != SelectionOrder.RANDOM) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") with nearbyOriginEntitySelector ("  + originEntitySelectorConfig
                    + ") and nearbyDistanceMeterClass ("  + nearbyDistanceMeterClass
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.ORIGINAL + " or " + SelectionOrder.RANDOM + ".");
        }
        if (resolvedCacheType.isCached()) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") with nearbyOriginEntitySelector ("  + originEntitySelectorConfig
                    + ") and nearbyDistanceMeterClass ("  + nearbyDistanceMeterClass
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
        NearbyRandom nearbyRandom = buildNearbyRandom();
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
        NearbyRandom nearbyRandom = buildNearbyRandom();
        return new NearEntityNearbyValueSelector(valueSelector, originEntitySelector,
                nearbyDistanceMeter, nearbyRandom, randomSelection);
    }

    protected NearbyRandom buildNearbyRandom() {
        boolean blockDistributionEnabled = blockDistributionSizeMinimum != null
                || blockDistributionSizeMaximum != null
                || blockDistributionSizeRatio != null
                || blockDistributionUniformDistributionProbability != null;
        boolean linearDistributionEnabled = linearDistributionSizeMaximum != null;
        boolean parabolicDistributionEnabled = parabolicDistributionSizeMaximum != null;
        boolean betaDistributionEnabled = betaDistributionAlpha != null
                || betaDistributionBeta != null;
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
            int sizeMinimum = blockDistributionSizeMinimum == null ? 0 : blockDistributionSizeMinimum;
            int sizeMaximum = blockDistributionSizeMaximum == null ? Integer.MAX_VALUE : blockDistributionSizeMaximum;
            double sizeRatio = blockDistributionSizeRatio == null ? 1.0 : blockDistributionSizeRatio;
            double uniformDistributionProbability = blockDistributionUniformDistributionProbability == null ? 0.0 : blockDistributionUniformDistributionProbability;
            return new BlockDistributionNearbyRandom(sizeMinimum, sizeMaximum, sizeRatio, uniformDistributionProbability);
        } else if (linearDistributionEnabled) {
            int sizeMaximum = linearDistributionSizeMaximum == null ? Integer.MAX_VALUE : linearDistributionSizeMaximum;
            return new LinearDistributionNearbyRandom(sizeMaximum);
        } else if (parabolicDistributionEnabled) {
            int sizeMaximum = parabolicDistributionSizeMaximum == null ? Integer.MAX_VALUE : parabolicDistributionSizeMaximum;
            return new ParabolicDistributionNearbyRandom(sizeMaximum);
        } else if (betaDistributionEnabled) {
            double alpha = betaDistributionAlpha == null ? 1.0 : betaDistributionAlpha;
            double beta = betaDistributionBeta == null ? 5.0 : betaDistributionBeta;
            return new BetaDistributionNearbyRandom(alpha, beta);
        } else {
            return new BetaDistributionNearbyRandom(1.0, 5.0);
        }
    }

    public void inherit(NearbySelectionConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (originEntitySelectorConfig == null) {
            originEntitySelectorConfig = inheritedConfig.getOriginEntitySelectorConfig();
        } else if (inheritedConfig.getOriginEntitySelectorConfig() != null) {
            originEntitySelectorConfig.inherit(inheritedConfig.getOriginEntitySelectorConfig());
        }
        nearbyDistanceMeterClass = ConfigUtils.inheritOverwritableProperty(nearbyDistanceMeterClass,
                inheritedConfig.getNearbyDistanceMeterClass());
        blockDistributionSizeMinimum = ConfigUtils.inheritOverwritableProperty(blockDistributionSizeMinimum,
                inheritedConfig.getBlockDistributionSizeMinimum());
        blockDistributionSizeMaximum = ConfigUtils.inheritOverwritableProperty(blockDistributionSizeMaximum,
                inheritedConfig.getBlockDistributionSizeMaximum());
        blockDistributionSizeRatio = ConfigUtils.inheritOverwritableProperty(blockDistributionSizeRatio,
                inheritedConfig.getBlockDistributionSizeRatio());
        blockDistributionUniformDistributionProbability = ConfigUtils.inheritOverwritableProperty(blockDistributionUniformDistributionProbability,
                inheritedConfig.getBlockDistributionUniformDistributionProbability());
        linearDistributionSizeMaximum = ConfigUtils.inheritOverwritableProperty(linearDistributionSizeMaximum,
                inheritedConfig.getLinearDistributionSizeMaximum());
        parabolicDistributionSizeMaximum = ConfigUtils.inheritOverwritableProperty(parabolicDistributionSizeMaximum,
                inheritedConfig.getParabolicDistributionSizeMaximum());
        betaDistributionAlpha = ConfigUtils.inheritOverwritableProperty(betaDistributionAlpha,
                inheritedConfig.getBetaDistributionAlpha());
        betaDistributionBeta = ConfigUtils.inheritOverwritableProperty(betaDistributionBeta,
                inheritedConfig.getBetaDistributionBeta());
    }

}
