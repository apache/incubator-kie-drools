package org.optaplanner.core.config.heuristic.selector.move.generic;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "valueSelectorConfig"
})
public class PillarChangeMoveSelectorConfig extends AbstractPillarMoveSelectorConfig<PillarChangeMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "pillarChangeMoveSelector";

    @XmlElement(name = "valueSelector")
    private ValueSelectorConfig valueSelectorConfig = null;

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    @Override
    public PillarChangeMoveSelectorConfig inherit(PillarChangeMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.getValueSelectorConfig());
        return this;
    }

    @Override
    public PillarChangeMoveSelectorConfig copyConfig() {
        return new PillarChangeMoveSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        if (valueSelectorConfig != null) {
            valueSelectorConfig.visitReferencedClasses(classVisitor);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + pillarSelectorConfig + ", " + valueSelectorConfig + ")";
    }

}
