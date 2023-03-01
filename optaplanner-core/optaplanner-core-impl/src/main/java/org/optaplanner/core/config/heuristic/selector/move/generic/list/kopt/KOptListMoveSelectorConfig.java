package org.optaplanner.core.config.heuristic.selector.move.generic.list.kopt;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "minimumK",
        "maximumK",
})
public class KOptListMoveSelectorConfig extends MoveSelectorConfig<KOptListMoveSelectorConfig> {

    public static final String XML_ELEMENT_NAME = "kOptListMoveSelector";

    protected Integer minimumK = null;
    protected Integer maximumK = null;

    public Integer getMinimumK() {
        return minimumK;
    }

    public void setMinimumK(Integer minimumK) {
        this.minimumK = minimumK;
    }

    public Integer getMaximumK() {
        return maximumK;
    }

    public void setMaximumK(Integer maximumK) {
        this.maximumK = maximumK;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public KOptListMoveSelectorConfig withMinimumK(Integer minimumK) {
        this.minimumK = minimumK;
        return this;
    }

    public KOptListMoveSelectorConfig withMaximumK(Integer maximumK) {
        this.maximumK = maximumK;
        return this;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public KOptListMoveSelectorConfig inherit(KOptListMoveSelectorConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        this.minimumK = ConfigUtils.inheritOverwritableProperty(minimumK, inheritedConfig.minimumK);
        this.maximumK = ConfigUtils.inheritOverwritableProperty(maximumK, inheritedConfig.maximumK);
        return this;
    }

    @Override
    public KOptListMoveSelectorConfig copyConfig() {
        return new KOptListMoveSelectorConfig().inherit(this);
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
