package org.optaplanner.core.config.heuristic.selector.move.generic.chained;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.chained.SubChainSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "entityClass",
        "subChainSelectorConfig",
        "secondarySubChainSelectorConfig",
        "selectReversingMoveToo"
})
public class SubChainSwapMoveSelectorConfig extends MoveSelectorConfig<SubChainSwapMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "subChainSwapMoveSelector";

    private Class<?> entityClass = null;
    @XmlElement(name = "subChainSelector")
    private SubChainSelectorConfig subChainSelectorConfig = null;
    @XmlElement(name = "secondarySubChainSelector")
    private SubChainSelectorConfig secondarySubChainSelectorConfig = null;

    private Boolean selectReversingMoveToo = null;

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public SubChainSelectorConfig getSubChainSelectorConfig() {
        return subChainSelectorConfig;
    }

    public void setSubChainSelectorConfig(SubChainSelectorConfig subChainSelectorConfig) {
        this.subChainSelectorConfig = subChainSelectorConfig;
    }

    public SubChainSelectorConfig getSecondarySubChainSelectorConfig() {
        return secondarySubChainSelectorConfig;
    }

    public void setSecondarySubChainSelectorConfig(SubChainSelectorConfig secondarySubChainSelectorConfig) {
        this.secondarySubChainSelectorConfig = secondarySubChainSelectorConfig;
    }

    public Boolean getSelectReversingMoveToo() {
        return selectReversingMoveToo;
    }

    public void setSelectReversingMoveToo(Boolean selectReversingMoveToo) {
        this.selectReversingMoveToo = selectReversingMoveToo;
    }

    @Override
    public SubChainSwapMoveSelectorConfig inherit(SubChainSwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        entityClass = ConfigUtils.inheritOverwritableProperty(entityClass, inheritedConfig.getEntityClass());
        subChainSelectorConfig = ConfigUtils.inheritConfig(subChainSelectorConfig, inheritedConfig.getSubChainSelectorConfig());
        secondarySubChainSelectorConfig = ConfigUtils.inheritConfig(secondarySubChainSelectorConfig,
                inheritedConfig.getSecondarySubChainSelectorConfig());
        selectReversingMoveToo = ConfigUtils.inheritOverwritableProperty(selectReversingMoveToo,
                inheritedConfig.getSelectReversingMoveToo());
        return this;
    }

    @Override
    public SubChainSwapMoveSelectorConfig copyConfig() {
        return new SubChainSwapMoveSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        classVisitor.accept(entityClass);
        if (subChainSelectorConfig != null) {
            subChainSelectorConfig.visitReferencedClasses(classVisitor);
        }
        if (secondarySubChainSelectorConfig != null) {
            secondarySubChainSelectorConfig.visitReferencedClasses(classVisitor);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subChainSelectorConfig
                + (secondarySubChainSelectorConfig == null ? "" : ", " + secondarySubChainSelectorConfig) + ")";
    }

}
