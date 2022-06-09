package org.optaplanner.core.config.heuristic.selector.common.nearby;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;

@XmlType(propOrder = {
        "originEntitySelectorConfig",
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

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (originEntitySelectorConfig != null) {
            originEntitySelectorConfig.visitReferencedClasses(classVisitor);
        }
        classVisitor.accept(nearbyDistanceMeterClass);
    }

}
