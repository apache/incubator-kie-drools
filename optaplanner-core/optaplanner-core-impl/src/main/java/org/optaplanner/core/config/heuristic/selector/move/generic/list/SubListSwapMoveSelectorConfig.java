package org.optaplanner.core.config.heuristic.selector.move.generic.list;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.list.SubListSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "minimumSubListSize",
        "maximumSubListSize",
        "selectReversingMoveToo",
        "subListSelectorConfig",
        "secondarySubListSelectorConfig"
})
public class SubListSwapMoveSelectorConfig extends MoveSelectorConfig<SubListSwapMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "subListSwapMoveSelector";

    /**
     * @deprecated The minimumSubListSize on the SubListSwapMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#getMinimumSubListSize()} instead.
     */
    @Deprecated(forRemoval = true)
    protected Integer minimumSubListSize = null;
    /**
     * @deprecated The maximumSubListSize on the SubListSwapMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#getMaximumSubListSize()} instead.
     */
    @Deprecated(forRemoval = true)
    protected Integer maximumSubListSize = null;
    private Boolean selectReversingMoveToo = null;
    @XmlElement(name = "subListSelector")
    private SubListSelectorConfig subListSelectorConfig = null;
    @XmlElement(name = "secondarySubListSelector")
    private SubListSelectorConfig secondarySubListSelectorConfig = null;

    /**
     * @deprecated The minimumSubListSize on the SubListSwapMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#getMinimumSubListSize()} instead.
     */
    @Deprecated(forRemoval = true)
    public Integer getMinimumSubListSize() {
        return minimumSubListSize;
    }

    /**
     * @deprecated The minimumSubListSize on the SubListSwapMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#setMinimumSubListSize(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    public void setMinimumSubListSize(Integer minimumSubListSize) {
        this.minimumSubListSize = minimumSubListSize;
    }

    /**
     * @deprecated The maximumSubListSize on the SubListSwapMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#getMaximumSubListSize()} instead.
     */
    @Deprecated(forRemoval = true)
    public Integer getMaximumSubListSize() {
        return maximumSubListSize;
    }

    /**
     * @deprecated The maximumSubListSize on the SubListSwapMoveSelectorConfig is deprecated and will be removed in a future
     *             major version of OptaPlanner. Use {@link SubListSelectorConfig#setMaximumSubListSize(Integer)} instead.
     */
    @Deprecated(forRemoval = true)
    public void setMaximumSubListSize(Integer maximumSubListSize) {
        this.maximumSubListSize = maximumSubListSize;
    }

    public Boolean getSelectReversingMoveToo() {
        return selectReversingMoveToo;
    }

    public void setSelectReversingMoveToo(Boolean selectReversingMoveToo) {
        this.selectReversingMoveToo = selectReversingMoveToo;
    }

    public SubListSelectorConfig getSubListSelectorConfig() {
        return subListSelectorConfig;
    }

    public void setSubListSelectorConfig(SubListSelectorConfig subListSelectorConfig) {
        this.subListSelectorConfig = subListSelectorConfig;
    }

    public SubListSelectorConfig getSecondarySubListSelectorConfig() {
        return secondarySubListSelectorConfig;
    }

    public void setSecondarySubListSelectorConfig(SubListSelectorConfig secondarySubListSelectorConfig) {
        this.secondarySubListSelectorConfig = secondarySubListSelectorConfig;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public SubListSwapMoveSelectorConfig withSelectReversingMoveToo(Boolean selectReversingMoveToo) {
        this.setSelectReversingMoveToo(selectReversingMoveToo);
        return this;
    }

    public SubListSwapMoveSelectorConfig withSubListSelectorConfig(SubListSelectorConfig subListSelectorConfig) {
        this.setSubListSelectorConfig(subListSelectorConfig);
        return this;
    }

    public SubListSwapMoveSelectorConfig
            withSecondarySubListSelectorConfig(SubListSelectorConfig secondarySubListSelectorConfig) {
        this.setSecondarySubListSelectorConfig(secondarySubListSelectorConfig);
        return this;
    }

    @Override
    public SubListSwapMoveSelectorConfig inherit(SubListSwapMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        this.minimumSubListSize =
                ConfigUtils.inheritOverwritableProperty(minimumSubListSize, inheritedConfig.minimumSubListSize);
        this.maximumSubListSize =
                ConfigUtils.inheritOverwritableProperty(maximumSubListSize, inheritedConfig.maximumSubListSize);
        this.selectReversingMoveToo =
                ConfigUtils.inheritOverwritableProperty(selectReversingMoveToo, inheritedConfig.selectReversingMoveToo);
        this.subListSelectorConfig =
                ConfigUtils.inheritOverwritableProperty(subListSelectorConfig, inheritedConfig.subListSelectorConfig);
        this.secondarySubListSelectorConfig =
                ConfigUtils.inheritOverwritableProperty(secondarySubListSelectorConfig,
                        inheritedConfig.secondarySubListSelectorConfig);
        return this;
    }

    @Override
    public SubListSwapMoveSelectorConfig copyConfig() {
        return new SubListSwapMoveSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        if (subListSelectorConfig != null) {
            subListSelectorConfig.visitReferencedClasses(classVisitor);
        }
        if (secondarySubListSelectorConfig != null) {
            secondarySubListSelectorConfig.visitReferencedClasses(classVisitor);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + subListSelectorConfig
                + (secondarySubListSelectorConfig == null ? "" : ", " + secondarySubListSelectorConfig) + ")";
    }
}
