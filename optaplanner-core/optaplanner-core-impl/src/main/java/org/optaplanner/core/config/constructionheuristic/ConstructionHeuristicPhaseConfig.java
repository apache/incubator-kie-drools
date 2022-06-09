package org.optaplanner.core.config.constructionheuristic;

import java.util.List;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

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
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.SubChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.TailChainSwapMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSorterManner;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "constructionHeuristicType",
        "entitySorterManner",
        "valueSorterManner",
        "entityPlacerConfig",
        "moveSelectorConfigList",
        "foragerConfig"
})
public class ConstructionHeuristicPhaseConfig extends PhaseConfig<ConstructionHeuristicPhaseConfig> {

    public static final String XML_ELEMENT_NAME = "constructionHeuristic";

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    protected ConstructionHeuristicType constructionHeuristicType = null;
    protected EntitySorterManner entitySorterManner = null;
    protected ValueSorterManner valueSorterManner = null;

    @XmlElements({
            @XmlElement(name = "queuedEntityPlacer", type = QueuedEntityPlacerConfig.class),
            @XmlElement(name = "queuedValuePlacer", type = QueuedValuePlacerConfig.class),
            @XmlElement(name = "pooledEntityPlacer", type = PooledEntityPlacerConfig.class)
    })
    protected EntityPlacerConfig entityPlacerConfig = null;

    /** Simpler alternative for {@link #entityPlacerConfig}. */
    @XmlElements({
            @XmlElement(name = CartesianProductMoveSelectorConfig.XML_ELEMENT_NAME,
                    type = CartesianProductMoveSelectorConfig.class),
            @XmlElement(name = ChangeMoveSelectorConfig.XML_ELEMENT_NAME, type = ChangeMoveSelectorConfig.class),
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
    protected List<MoveSelectorConfig> moveSelectorConfigList = null;

    @XmlElement(name = "forager")
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
        return entityPlacerConfig;
    }

    public void setEntityPlacerConfig(EntityPlacerConfig entityPlacerConfig) {
        this.entityPlacerConfig = entityPlacerConfig;
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

    public ConstructionHeuristicPhaseConfig withConstructionHeuristicType(
            ConstructionHeuristicType constructionHeuristicType) {
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

    public ConstructionHeuristicPhaseConfig withEntityPlacerConfig(EntityPlacerConfig<?> entityPlacerConfig) {
        this.entityPlacerConfig = entityPlacerConfig;
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

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (getTerminationConfig() != null) {
            getTerminationConfig().visitReferencedClasses(classVisitor);
        }
        if (entityPlacerConfig != null) {
            entityPlacerConfig.visitReferencedClasses(classVisitor);
        }
        if (moveSelectorConfigList != null) {
            moveSelectorConfigList.forEach(ms -> ms.visitReferencedClasses(classVisitor));
        }
        if (foragerConfig != null) {
            foragerConfig.visitReferencedClasses(classVisitor);
        }
    }

}
