package org.optaplanner.core.config.heuristic.selector.entity;

import java.util.Comparator;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

@XmlType(propOrder = {
        "id",
        "mimicSelectorRef",
        "entityClass",
        "cacheType",
        "selectionOrder",
        "nearbySelectionConfig",
        "filterClass",
        "sorterManner",
        "sorterComparatorClass",
        "sorterWeightFactoryClass",
        "sorterOrder",
        "sorterClass",
        "probabilityWeightFactoryClass",
        "selectedCountLimit"
})
public class EntitySelectorConfig extends SelectorConfig<EntitySelectorConfig> {

    public static EntitySelectorConfig newMimicSelectorConfig(String mimicSelectorRef) {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig();
        entitySelectorConfig.setMimicSelectorRef(mimicSelectorRef);
        return entitySelectorConfig;
    }

    @XmlAttribute
    protected String id = null;
    @XmlAttribute
    protected String mimicSelectorRef = null;

    protected Class<?> entityClass = null;

    protected SelectionCacheType cacheType = null;
    protected SelectionOrder selectionOrder = null;

    @XmlElement(name = "nearbySelection")
    protected NearbySelectionConfig nearbySelectionConfig = null;

    protected Class<? extends SelectionFilter> filterClass = null;

    protected EntitySorterManner sorterManner = null;
    protected Class<? extends Comparator> sorterComparatorClass = null;
    protected Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass = null;
    protected SelectionSorterOrder sorterOrder = null;
    protected Class<? extends SelectionSorter> sorterClass = null;

    protected Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass = null;

    protected Long selectedCountLimit = null;

    public EntitySelectorConfig() {
    }

    public EntitySelectorConfig(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public EntitySelectorConfig(EntitySelectorConfig inheritedConfig) {
        if (inheritedConfig != null) {
            inherit(inheritedConfig);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMimicSelectorRef() {
        return mimicSelectorRef;
    }

    public void setMimicSelectorRef(String mimicSelectorRef) {
        this.mimicSelectorRef = mimicSelectorRef;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    public void setCacheType(SelectionCacheType cacheType) {
        this.cacheType = cacheType;
    }

    public SelectionOrder getSelectionOrder() {
        return selectionOrder;
    }

    public void setSelectionOrder(SelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    public NearbySelectionConfig getNearbySelectionConfig() {
        return nearbySelectionConfig;
    }

    public void setNearbySelectionConfig(NearbySelectionConfig nearbySelectionConfig) {
        this.nearbySelectionConfig = nearbySelectionConfig;
    }

    public Class<? extends SelectionFilter> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends SelectionFilter> filterClass) {
        this.filterClass = filterClass;
    }

    public EntitySorterManner getSorterManner() {
        return sorterManner;
    }

    public void setSorterManner(EntitySorterManner sorterManner) {
        this.sorterManner = sorterManner;
    }

    public Class<? extends Comparator> getSorterComparatorClass() {
        return sorterComparatorClass;
    }

    public void setSorterComparatorClass(Class<? extends Comparator> sorterComparatorClass) {
        this.sorterComparatorClass = sorterComparatorClass;
    }

    public Class<? extends SelectionSorterWeightFactory> getSorterWeightFactoryClass() {
        return sorterWeightFactoryClass;
    }

    public void setSorterWeightFactoryClass(Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass) {
        this.sorterWeightFactoryClass = sorterWeightFactoryClass;
    }

    public SelectionSorterOrder getSorterOrder() {
        return sorterOrder;
    }

    public void setSorterOrder(SelectionSorterOrder sorterOrder) {
        this.sorterOrder = sorterOrder;
    }

    public Class<? extends SelectionSorter> getSorterClass() {
        return sorterClass;
    }

    public void setSorterClass(Class<? extends SelectionSorter> sorterClass) {
        this.sorterClass = sorterClass;
    }

    public Class<? extends SelectionProbabilityWeightFactory> getProbabilityWeightFactoryClass() {
        return probabilityWeightFactoryClass;
    }

    public void setProbabilityWeightFactoryClass(
            Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass) {
        this.probabilityWeightFactoryClass = probabilityWeightFactoryClass;
    }

    public Long getSelectedCountLimit() {
        return selectedCountLimit;
    }

    public void setSelectedCountLimit(Long selectedCountLimit) {
        this.selectedCountLimit = selectedCountLimit;
    }

    @Override
    public EntitySelectorConfig inherit(EntitySelectorConfig inheritedConfig) {
        id = ConfigUtils.inheritOverwritableProperty(id, inheritedConfig.getId());
        mimicSelectorRef = ConfigUtils.inheritOverwritableProperty(mimicSelectorRef,
                inheritedConfig.getMimicSelectorRef());
        entityClass = ConfigUtils.inheritOverwritableProperty(entityClass,
                inheritedConfig.getEntityClass());
        nearbySelectionConfig = ConfigUtils.inheritConfig(nearbySelectionConfig, inheritedConfig.getNearbySelectionConfig());
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
        filterClass = ConfigUtils.inheritOverwritableProperty(
                filterClass, inheritedConfig.getFilterClass());
        sorterManner = ConfigUtils.inheritOverwritableProperty(
                sorterManner, inheritedConfig.getSorterManner());
        sorterComparatorClass = ConfigUtils.inheritOverwritableProperty(
                sorterComparatorClass, inheritedConfig.getSorterComparatorClass());
        sorterWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                sorterWeightFactoryClass, inheritedConfig.getSorterWeightFactoryClass());
        sorterOrder = ConfigUtils.inheritOverwritableProperty(
                sorterOrder, inheritedConfig.getSorterOrder());
        sorterClass = ConfigUtils.inheritOverwritableProperty(
                sorterClass, inheritedConfig.getSorterClass());
        probabilityWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(
                probabilityWeightFactoryClass, inheritedConfig.getProbabilityWeightFactoryClass());
        selectedCountLimit = ConfigUtils.inheritOverwritableProperty(
                selectedCountLimit, inheritedConfig.getSelectedCountLimit());
        return this;
    }

    @Override
    public EntitySelectorConfig copyConfig() {
        return new EntitySelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        classVisitor.accept(entityClass);
        if (nearbySelectionConfig != null) {
            nearbySelectionConfig.visitReferencedClasses(classVisitor);
        }
        classVisitor.accept(filterClass);
        classVisitor.accept(sorterComparatorClass);
        classVisitor.accept(sorterWeightFactoryClass);
        classVisitor.accept(sorterClass);
        classVisitor.accept(probabilityWeightFactoryClass);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entityClass + ")";
    }

    public static <Solution_> boolean hasSorter(EntitySorterManner entitySorterManner,
            EntityDescriptor<Solution_> entityDescriptor) {
        switch (entitySorterManner) {
            case NONE:
                return false;
            case DECREASING_DIFFICULTY:
                return true;
            case DECREASING_DIFFICULTY_IF_AVAILABLE:
                return entityDescriptor.getDecreasingDifficultySorter() != null;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + entitySorterManner + ") is not implemented.");
        }
    }

    public static <Solution_, T> SelectionSorter<Solution_, T> determineSorter(EntitySorterManner entitySorterManner,
            EntityDescriptor<Solution_> entityDescriptor) {
        SelectionSorter<Solution_, T> sorter;
        switch (entitySorterManner) {
            case NONE:
                throw new IllegalStateException("Impossible state: hasSorter() should have returned null.");
            case DECREASING_DIFFICULTY:
            case DECREASING_DIFFICULTY_IF_AVAILABLE:
                sorter = (SelectionSorter<Solution_, T>) entityDescriptor.getDecreasingDifficultySorter();
                if (sorter == null) {
                    throw new IllegalArgumentException("The sorterManner (" + entitySorterManner
                            + ") on entity class (" + entityDescriptor.getEntityClass()
                            + ") fails because that entity class's @" + PlanningEntity.class.getSimpleName()
                            + " annotation does not declare any difficulty comparison.");
                }
                return sorter;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + entitySorterManner + ") is not implemented.");
        }
    }

}
