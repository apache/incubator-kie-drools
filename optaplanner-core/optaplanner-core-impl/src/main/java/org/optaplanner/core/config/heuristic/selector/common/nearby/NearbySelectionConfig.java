/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.config.heuristic.selector.common.nearby;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.list.SubListSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;

@XmlType(propOrder = {
        "originEntitySelectorConfig",
        "originSubListSelectorConfig",
        "originValueSelectorConfig",
        "nearbyDistanceMeterClass",
        "nearbySelectionDistributionType",
        "blockDistributionSizeMinimum",
        "blockDistributionSizeMaximum",
        "blockDistributionSizeRatio",
        "blockDistributionUniformDistributionProbability",
        "linearDistributionSizeMaximum",
        "parabolicDistributionSizeMaximum",
        "betaDistributionAlpha",
        "betaDistributionBeta"
})
public class NearbySelectionConfig extends SelectorConfig<NearbySelectionConfig> {

    @XmlElement(name = "originEntitySelector")
    protected EntitySelectorConfig originEntitySelectorConfig = null;
    @XmlElement(name = "originSubListSelector")
    protected SubListSelectorConfig originSubListSelectorConfig = null;
    @XmlElement(name = "originValueSelector")
    protected ValueSelectorConfig originValueSelectorConfig = null;
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

    public SubListSelectorConfig getOriginSubListSelectorConfig() {
        return originSubListSelectorConfig;
    }

    public void setOriginSubListSelectorConfig(SubListSelectorConfig originSubListSelectorConfig) {
        this.originSubListSelectorConfig = originSubListSelectorConfig;
    }

    public ValueSelectorConfig getOriginValueSelectorConfig() {
        return originValueSelectorConfig;
    }

    public void setOriginValueSelectorConfig(ValueSelectorConfig originValueSelectorConfig) {
        this.originValueSelectorConfig = originValueSelectorConfig;
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

    // ************************************************************************
    // With methods
    // ************************************************************************

    public NearbySelectionConfig withOriginEntitySelectorConfig(EntitySelectorConfig originEntitySelectorConfig) {
        this.setOriginEntitySelectorConfig(originEntitySelectorConfig);
        return this;
    }

    public NearbySelectionConfig withOriginSubListSelectorConfig(SubListSelectorConfig originSubListSelectorConfig) {
        this.setOriginSubListSelectorConfig(originSubListSelectorConfig);
        return this;
    }

    public NearbySelectionConfig withOriginValueSelectorConfig(ValueSelectorConfig originValueSelectorConfig) {
        this.setOriginValueSelectorConfig(originValueSelectorConfig);
        return this;
    }

    public NearbySelectionConfig withNearbyDistanceMeterClass(Class<? extends NearbyDistanceMeter> nearbyDistanceMeterClass) {
        this.setNearbyDistanceMeterClass(nearbyDistanceMeterClass);
        return this;
    }

    public NearbySelectionConfig
            withNearbySelectionDistributionType(NearbySelectionDistributionType nearbySelectionDistributionType) {
        this.setNearbySelectionDistributionType(nearbySelectionDistributionType);
        return this;
    }

    public NearbySelectionConfig withBlockDistributionSizeMinimum(Integer blockDistributionSizeMinimum) {
        this.setBlockDistributionSizeMinimum(blockDistributionSizeMinimum);
        return this;
    }

    public NearbySelectionConfig withBlockDistributionSizeMaximum(Integer blockDistributionSizeMaximum) {
        this.setBlockDistributionSizeMaximum(blockDistributionSizeMaximum);
        return this;
    }

    public NearbySelectionConfig withBlockDistributionSizeRatio(Double blockDistributionSizeRatio) {
        this.setBlockDistributionSizeRatio(blockDistributionSizeRatio);
        return this;
    }

    public NearbySelectionConfig
            withBlockDistributionUniformDistributionProbability(Double blockDistributionUniformDistributionProbability) {
        this.setBlockDistributionUniformDistributionProbability(blockDistributionUniformDistributionProbability);
        return this;
    }

    public NearbySelectionConfig withLinearDistributionSizeMaximum(Integer linearDistributionSizeMaximum) {
        this.setLinearDistributionSizeMaximum(linearDistributionSizeMaximum);
        return this;
    }

    public NearbySelectionConfig withParabolicDistributionSizeMaximum(Integer parabolicDistributionSizeMaximum) {
        this.setParabolicDistributionSizeMaximum(parabolicDistributionSizeMaximum);
        return this;
    }

    public NearbySelectionConfig withBetaDistributionAlpha(Double betaDistributionAlpha) {
        this.setBetaDistributionAlpha(betaDistributionAlpha);
        return this;
    }

    public NearbySelectionConfig withBetaDistributionBeta(Double betaDistributionBeta) {
        this.setBetaDistributionBeta(betaDistributionBeta);
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public void validateNearby(SelectionCacheType resolvedCacheType, SelectionOrder resolvedSelectionOrder) {
        long originSelectorCount = Stream.of(originEntitySelectorConfig, originSubListSelectorConfig, originValueSelectorConfig)
                .filter(Objects::nonNull)
                .count();
        if (originSelectorCount == 0) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") is nearby selection but lacks an origin selector config."
                    + " Set one of originEntitySelectorConfig, originSubListSelectorConfig or originValueSelectorConfig.");
        } else if (originSelectorCount > 1) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has multiple origin selector configs but exactly one is expected."
                    + " Set one of originEntitySelectorConfig, originSubListSelectorConfig or originValueSelectorConfig.");
        }
        if (originEntitySelectorConfig != null &&
                originEntitySelectorConfig.getMimicSelectorRef() == null) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has an originEntitySelectorConfig (" + originEntitySelectorConfig
                    + ") which has no MimicSelectorRef (" + originEntitySelectorConfig.getMimicSelectorRef() + "). "
                    + "A nearby's original entity should always be the same as an entity selected earlier in the move.");
        }
        if (originSubListSelectorConfig != null &&
                originSubListSelectorConfig.getMimicSelectorRef() == null) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has an originSubListSelectorConfig (" + originSubListSelectorConfig
                    + ") which has no MimicSelectorRef (" + originSubListSelectorConfig.getMimicSelectorRef() + "). "
                    + "A nearby's original subList should always be the same as a subList selected earlier in the move.");
        }
        if (originValueSelectorConfig != null &&
                originValueSelectorConfig.getMimicSelectorRef() == null) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") has an originValueSelectorConfig (" + originValueSelectorConfig
                    + ") which has no MimicSelectorRef (" + originValueSelectorConfig.getMimicSelectorRef() + "). "
                    + "A nearby's original value should always be the same as a value selected earlier in the move.");
        }
        if (nearbyDistanceMeterClass == null) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") is nearby selection but lacks a nearbyDistanceMeterClass (" + nearbyDistanceMeterClass + ").");
        }
        if (resolvedSelectionOrder != SelectionOrder.ORIGINAL && resolvedSelectionOrder != SelectionOrder.RANDOM) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") with originEntitySelector (" + originEntitySelectorConfig
                    + ") and originSubListSelector (" + originSubListSelectorConfig
                    + ") and originValueSelector (" + originValueSelectorConfig
                    + ") and nearbyDistanceMeterClass (" + nearbyDistanceMeterClass
                    + ") has a resolvedSelectionOrder (" + resolvedSelectionOrder
                    + ") that is not " + SelectionOrder.ORIGINAL + " or " + SelectionOrder.RANDOM + ".");
        }
        if (resolvedCacheType.isCached()) {
            throw new IllegalArgumentException("The nearbySelectorConfig (" + this
                    + ") with originEntitySelector (" + originEntitySelectorConfig
                    + ") and originSubListSelector (" + originSubListSelectorConfig
                    + ") and originValueSelector (" + originValueSelectorConfig
                    + ") and nearbyDistanceMeterClass (" + nearbyDistanceMeterClass
                    + ") has a resolvedCacheType (" + resolvedCacheType
                    + ") that is cached.");
        }
    }

    @Override
    public NearbySelectionConfig inherit(NearbySelectionConfig inheritedConfig) {
        originEntitySelectorConfig = ConfigUtils.inheritConfig(originEntitySelectorConfig,
                inheritedConfig.getOriginEntitySelectorConfig());
        originSubListSelectorConfig = ConfigUtils.inheritConfig(originSubListSelectorConfig,
                inheritedConfig.getOriginSubListSelectorConfig());
        originValueSelectorConfig = ConfigUtils.inheritConfig(originValueSelectorConfig,
                inheritedConfig.getOriginValueSelectorConfig());
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

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (originEntitySelectorConfig != null) {
            originEntitySelectorConfig.visitReferencedClasses(classVisitor);
        }
        if (originSubListSelectorConfig != null) {
            originSubListSelectorConfig.visitReferencedClasses(classVisitor);
        }
        if (originValueSelectorConfig != null) {
            originValueSelectorConfig.visitReferencedClasses(classVisitor);
        }
        classVisitor.accept(nearbyDistanceMeterClass);
    }

}
