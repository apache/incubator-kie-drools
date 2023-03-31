package org.optaplanner.core.config.heuristic.selector.move.generic.list;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "valueSelectorConfig",
        "secondaryValueSelectorConfig"
})
public class ListSwapMoveSelectorConfig extends MoveSelectorConfig<ListSwapMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "listSwapMoveSelector";

    @XmlElement(name = "valueSelector")
    private ValueSelectorConfig valueSelectorConfig = null;
    @XmlElement(name = "secondaryValueSelector")
    private ValueSelectorConfig secondaryValueSelectorConfig = null;

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    public ValueSelectorConfig getSecondaryValueSelectorConfig() {
        return secondaryValueSelectorConfig;
    }

    public void setSecondaryValueSelectorConfig(ValueSelectorConfig secondaryValueSelectorConfig) {
        this.secondaryValueSelectorConfig = secondaryValueSelectorConfig;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public ListSwapMoveSelectorConfig withValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.setValueSelectorConfig(valueSelectorConfig);
        return this;
    }

    public ListSwapMoveSelectorConfig withSecondaryValueSelectorConfig(ValueSelectorConfig secondaryValueSelectorConfig) {
        this.setSecondaryValueSelectorConfig(secondaryValueSelectorConfig);
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public ListSwapMoveSelectorConfig inherit(ListSwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.getValueSelectorConfig());
        secondaryValueSelectorConfig = ConfigUtils.inheritConfig(secondaryValueSelectorConfig,
                inheritedConfig.getSecondaryValueSelectorConfig());
        return this;
    }

    @Override
    public ListSwapMoveSelectorConfig copyConfig() {
        return new ListSwapMoveSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        if (valueSelectorConfig != null) {
            valueSelectorConfig.visitReferencedClasses(classVisitor);
        }
        if (secondaryValueSelectorConfig != null) {
            secondaryValueSelectorConfig.visitReferencedClasses(classVisitor);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valueSelectorConfig
                + (secondaryValueSelectorConfig == null ? "" : ", " + secondaryValueSelectorConfig) + ")";
    }

}
