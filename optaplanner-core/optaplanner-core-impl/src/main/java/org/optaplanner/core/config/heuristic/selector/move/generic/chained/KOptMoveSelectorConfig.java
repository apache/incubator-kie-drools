package org.optaplanner.core.config.heuristic.selector.move.generic.chained;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

/**
 * THIS CLASS IS EXPERIMENTAL AND UNSUPPORTED.
 * Backward compatibility is not guaranteed.
 * It's NOT DOCUMENTED because we'll only document it when it actually works in more than 1 use case.
 *
 * Do not use.
 *
 * @see TailChainSwapMoveSelectorConfig
 */
@XmlType(propOrder = {
        "entitySelectorConfig",
        "valueSelectorConfig"
})
public class KOptMoveSelectorConfig extends MoveSelectorConfig<KOptMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "kOptMoveSelector";

    @XmlElement(name = "entitySelector")
    private EntitySelectorConfig entitySelectorConfig = null;
    /**
     * Like {@link TailChainSwapMoveSelectorConfig#valueSelectorConfig} but used multiple times to create 1 move.
     */
    @XmlElement(name = "valueSelector")
    private ValueSelectorConfig valueSelectorConfig = null;

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public ValueSelectorConfig getValueSelectorConfig() {
        return valueSelectorConfig;
    }

    public void setValueSelectorConfig(ValueSelectorConfig valueSelectorConfig) {
        this.valueSelectorConfig = valueSelectorConfig;
    }

    @Override
    public KOptMoveSelectorConfig inherit(KOptMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        entitySelectorConfig = ConfigUtils.inheritConfig(entitySelectorConfig, inheritedConfig.getEntitySelectorConfig());
        valueSelectorConfig = ConfigUtils.inheritConfig(valueSelectorConfig, inheritedConfig.getValueSelectorConfig());
        return this;
    }

    @Override
    public KOptMoveSelectorConfig copyConfig() {
        return new KOptMoveSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        if (entitySelectorConfig != null) {
            entitySelectorConfig.visitReferencedClasses(classVisitor);
        }
        if (valueSelectorConfig != null) {
            valueSelectorConfig.visitReferencedClasses(classVisitor);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ", " + valueSelectorConfig + ")";
    }

}
