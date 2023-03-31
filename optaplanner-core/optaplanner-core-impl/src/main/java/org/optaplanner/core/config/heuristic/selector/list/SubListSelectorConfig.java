package org.optaplanner.core.config.heuristic.selector.list;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.common.nearby.NearbySelectionConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "id",
        "mimicSelectorRef",
        "valueSelectorConfig",
        "nearbySelectionConfig",
        "minimumSubListSize",
        "maximumSubListSize",
})
public class SubListSelectorConfig extends SelectorConfig<SubListSelectorConfig> {

    @XmlAttribute
    private String id = null;
    @XmlAttribute
    private String mimicSelectorRef = null;

    @XmlElement(name = "valueSelector")
    private ValueSelectorConfig valueSelectorConfig = null;
    @XmlElement(name = "nearbySelection")
    private NearbySelectionConfig nearbySelectionConfig = null;

    private Integer minimumSubListSize = null;
    private Integer maximumSubListSize = null;

    public SubListSelectorConfig() {
    }

    public SubListSelectorConfig(SubListSelectorConfig inheritedConfig) {
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

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    public NearbySelectionConfig getNearbySelectionConfig() {
        return nearbySelectionConfig;
    }

    public void setNearbySelectionConfig(NearbySelectionConfig nearbySelectionConfig) {
        this.nearbySelectionConfig = nearbySelectionConfig;
    }

    public Integer getMinimumSubListSize() {
        return minimumSubListSize;
    }

    public void setMinimumSubListSize(Integer minimumSubListSize) {
        this.minimumSubListSize = minimumSubListSize;
    }

    public Integer getMaximumSubListSize() {
        return maximumSubListSize;
    }

    public void setMaximumSubListSize(Integer maximumSubListSize) {
        this.maximumSubListSize = maximumSubListSize;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public SubListSelectorConfig withId(String id) {
        this.setId(id);
        return this;
    }

    public SubListSelectorConfig withMimicSelectorRef(String mimicSelectorRef) {
        this.setMimicSelectorRef(mimicSelectorRef);
        return this;
    }

    public SubListSelectorConfig withValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.setValueSelectorConfig(valueSelectorConfig);
        return this;
    }

    public SubListSelectorConfig withNearbySelectionConfig(NearbySelectionConfig nearbySelectionConfig) {
        this.setNearbySelectionConfig(nearbySelectionConfig);
        return this;
    }

    public SubListSelectorConfig withMinimumSubListSize(Integer minimumSubListSize) {
        this.setMinimumSubListSize(minimumSubListSize);
        return this;
    }

    public SubListSelectorConfig withMaximumSubListSize(Integer maximumSubListSize) {
        this.setMaximumSubListSize(maximumSubListSize);
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public SubListSelectorConfig inherit(SubListSelectorConfig inheritedConfig) {
        id = ConfigUtils.inheritOverwritableProperty(id, inheritedConfig.id);
        mimicSelectorRef = ConfigUtils.inheritOverwritableProperty(mimicSelectorRef, inheritedConfig.mimicSelectorRef);
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.valueSelectorConfig);
        nearbySelectionConfig = ConfigUtils.inheritConfig(nearbySelectionConfig, inheritedConfig.nearbySelectionConfig);
        minimumSubListSize = ConfigUtils.inheritOverwritableProperty(minimumSubListSize, inheritedConfig.minimumSubListSize);
        maximumSubListSize = ConfigUtils.inheritOverwritableProperty(maximumSubListSize, inheritedConfig.maximumSubListSize);
        return this;
    }

    @Override
    public SubListSelectorConfig copyConfig() {
        return new SubListSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (valueSelectorConfig != null) {
            valueSelectorConfig.visitReferencedClasses(classVisitor);
        }
        if (nearbySelectionConfig != null) {
            nearbySelectionConfig.visitReferencedClasses(classVisitor);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelectorConfig + ")";
    }
}
