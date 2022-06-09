package org.optaplanner.core.config.heuristic.selector.value;

import java.util.Comparator;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.common.decorator.SelectionSorterOrder;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionProbabilityWeightFactory;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;

@XmlType(propOrder = {
        "id",
        "mimicSelectorRef",
        "downcastEntityClass",
        "variableName",
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
public class ValueSelectorConfig extends SelectorConfig<ValueSelectorConfig> {

    @XmlAttribute
    protected String id = null;
    @XmlAttribute
    protected String mimicSelectorRef = null;

    protected Class<?> downcastEntityClass = null;
    @XmlAttribute
    protected String variableName = null;

    protected SelectionCacheType cacheType = null;
    protected SelectionOrder selectionOrder = null;

    @XmlElement(name = "nearbySelection")
    protected NearbySelectionConfig nearbySelectionConfig = null;

    protected Class<? extends SelectionFilter> filterClass = null;

    protected ValueSorterManner sorterManner = null;
    protected Class<? extends Comparator> sorterComparatorClass = null;
    protected Class<? extends SelectionSorterWeightFactory> sorterWeightFactoryClass = null;
    protected SelectionSorterOrder sorterOrder = null;
    protected Class<? extends SelectionSorter> sorterClass = null;

    protected Class<? extends SelectionProbabilityWeightFactory> probabilityWeightFactoryClass = null;

    protected Long selectedCountLimit = null;

    public ValueSelectorConfig() {
    }

    public ValueSelectorConfig(String variableName) {
        this.variableName = variableName;
    }

    public ValueSelectorConfig(ValueSelectorConfig inheritedConfig) {
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

    public Class<?> getDowncastEntityClass() {
        return downcastEntityClass;
    }

    public void setDowncastEntityClass(Class<?> downcastEntityClass) {
        this.downcastEntityClass = downcastEntityClass;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
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

    public ValueSorterManner getSorterManner() {
        return sorterManner;
    }

    public void setSorterManner(ValueSorterManner sorterManner) {
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
    public ValueSelectorConfig inherit(ValueSelectorConfig inheritedConfig) {
        id = ConfigUtils.inheritOverwritableProperty(id, inheritedConfig.getId());
        mimicSelectorRef = ConfigUtils.inheritOverwritableProperty(mimicSelectorRef,
                inheritedConfig.getMimicSelectorRef());
        downcastEntityClass = ConfigUtils.inheritOverwritableProperty(downcastEntityClass,
                inheritedConfig.getDowncastEntityClass());
        variableName = ConfigUtils.inheritOverwritableProperty(variableName, inheritedConfig.getVariableName());
        nearbySelectionConfig = ConfigUtils.inheritConfig(nearbySelectionConfig, inheritedConfig.getNearbySelectionConfig());
        filterClass = ConfigUtils.inheritOverwritableProperty(filterClass, inheritedConfig.getFilterClass());
        cacheType = ConfigUtils.inheritOverwritableProperty(cacheType, inheritedConfig.getCacheType());
        selectionOrder = ConfigUtils.inheritOverwritableProperty(selectionOrder, inheritedConfig.getSelectionOrder());
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
    public ValueSelectorConfig copyConfig() {
        return new ValueSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        classVisitor.accept(downcastEntityClass);
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
        return getClass().getSimpleName() + "(" + variableName + ")";
    }

    public static <Solution_> boolean hasSorter(ValueSorterManner valueSorterManner,
            GenuineVariableDescriptor<Solution_> variableDescriptor) {
        switch (valueSorterManner) {
            case NONE:
                return false;
            case INCREASING_STRENGTH:
            case DECREASING_STRENGTH:
                return true;
            case INCREASING_STRENGTH_IF_AVAILABLE:
                return variableDescriptor.getIncreasingStrengthSorter() != null;
            case DECREASING_STRENGTH_IF_AVAILABLE:
                return variableDescriptor.getDecreasingStrengthSorter() != null;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + valueSorterManner + ") is not implemented.");
        }
    }

    public static <Solution_> SelectionSorter<Solution_, Object> determineSorter(ValueSorterManner valueSorterManner,
            GenuineVariableDescriptor<Solution_> variableDescriptor) {
        SelectionSorter<Solution_, Object> sorter;
        switch (valueSorterManner) {
            case NONE:
                throw new IllegalStateException("Impossible state: hasSorter() should have returned null.");
            case INCREASING_STRENGTH:
            case INCREASING_STRENGTH_IF_AVAILABLE:
                sorter = variableDescriptor.getIncreasingStrengthSorter();
                break;
            case DECREASING_STRENGTH:
            case DECREASING_STRENGTH_IF_AVAILABLE:
                sorter = variableDescriptor.getDecreasingStrengthSorter();
                break;
            default:
                throw new IllegalStateException("The sorterManner ("
                        + valueSorterManner + ") is not implemented.");
        }
        if (sorter == null) {
            throw new IllegalArgumentException("The sorterManner (" + valueSorterManner
                    + ") on entity class (" + variableDescriptor.getEntityDescriptor().getEntityClass()
                    + ")'s variable (" + variableDescriptor.getVariableName()
                    + ") fails because that variable getter's @" + PlanningVariable.class.getSimpleName()
                    + " annotation does not declare any strength comparison.");
        }
        return sorter;
    }

}
