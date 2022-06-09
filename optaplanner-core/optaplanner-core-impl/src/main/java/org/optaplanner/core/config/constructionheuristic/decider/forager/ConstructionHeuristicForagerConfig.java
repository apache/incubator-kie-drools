package org.optaplanner.core.config.constructionheuristic.decider.forager;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "pickEarlyType"
})
public class ConstructionHeuristicForagerConfig extends AbstractConfig<ConstructionHeuristicForagerConfig> {

    private ConstructionHeuristicPickEarlyType pickEarlyType = null;

    public ConstructionHeuristicPickEarlyType getPickEarlyType() {
        return pickEarlyType;
    }

    public void setPickEarlyType(ConstructionHeuristicPickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
    }

    @Override
    public ConstructionHeuristicForagerConfig inherit(ConstructionHeuristicForagerConfig inheritedConfig) {
        pickEarlyType = ConfigUtils.inheritOverwritableProperty(pickEarlyType, inheritedConfig.getPickEarlyType());
        return this;
    }

    @Override
    public ConstructionHeuristicForagerConfig copyConfig() {
        return new ConstructionHeuristicForagerConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        // No referenced classes
    }

}
