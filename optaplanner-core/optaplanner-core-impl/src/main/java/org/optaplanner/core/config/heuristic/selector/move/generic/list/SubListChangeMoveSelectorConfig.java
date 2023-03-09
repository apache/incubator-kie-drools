package org.optaplanner.core.config.heuristic.selector.move.generic.list;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "minimumSubListSize",
        "maximumSubListSize",
        "selectReversingMoveToo"
})
public class SubListChangeMoveSelectorConfig extends MoveSelectorConfig<SubListChangeMoveSelectorConfig>
        implements SubListSelectorConfig {

    public static final String XML_ELEMENT_NAME = "subListChangeMoveSelector";

    protected Integer minimumSubListSize = null;
    protected Integer maximumSubListSize = null;
    private Boolean selectReversingMoveToo = null;

    @Override
    public Integer getMinimumSubListSize() {
        return minimumSubListSize;
    }

    public void setMinimumSubListSize(Integer minimumSubListSize) {
        this.minimumSubListSize = minimumSubListSize;
    }

    @Override
    public Integer getMaximumSubListSize() {
        return maximumSubListSize;
    }

    public void setMaximumSubListSize(Integer maximumSubListSize) {
        this.maximumSubListSize = maximumSubListSize;
    }

    public Boolean getSelectReversingMoveToo() {
        return selectReversingMoveToo;
    }

    public void setSelectReversingMoveToo(Boolean selectReversingMoveToo) {
        this.selectReversingMoveToo = selectReversingMoveToo;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public SubListChangeMoveSelectorConfig withMinimumSubListSize(Integer minimumSubListSize) {
        this.setMinimumSubListSize(minimumSubListSize);
        return this;
    }

    public SubListChangeMoveSelectorConfig withMaximumSubListSize(Integer maximumSubListSize) {
        this.setMaximumSubListSize(maximumSubListSize);
        return this;
    }

    public SubListChangeMoveSelectorConfig withSelectReversingMoveToo(Boolean selectReversingMoveToo) {
        this.setSelectReversingMoveToo(selectReversingMoveToo);
        return this;
    }

    @Override
    public SubListChangeMoveSelectorConfig inherit(SubListChangeMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        this.minimumSubListSize =
                ConfigUtils.inheritOverwritableProperty(minimumSubListSize, inheritedConfig.minimumSubListSize);
        this.maximumSubListSize =
                ConfigUtils.inheritOverwritableProperty(maximumSubListSize, inheritedConfig.maximumSubListSize);
        this.selectReversingMoveToo =
                ConfigUtils.inheritOverwritableProperty(selectReversingMoveToo, inheritedConfig.selectReversingMoveToo);
        return this;
    }

    @Override
    public SubListChangeMoveSelectorConfig copyConfig() {
        return new SubListChangeMoveSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "()";
    }
}
