package org.optaplanner.core.config.heuristic.selector.entity.pillar;

import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.heuristic.selector.SelectorConfig;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "entitySelectorConfig",
        "minimumSubPillarSize",
        "maximumSubPillarSize"
})
public class PillarSelectorConfig extends SelectorConfig<PillarSelectorConfig> {

    @XmlElement(name = "entitySelector")
    protected EntitySelectorConfig entitySelectorConfig = null;

    protected Integer minimumSubPillarSize = null;
    protected Integer maximumSubPillarSize = null;

    public EntitySelectorConfig getEntitySelectorConfig() {
        return entitySelectorConfig;
    }

    public void setEntitySelectorConfig(EntitySelectorConfig entitySelectorConfig) {
        this.entitySelectorConfig = entitySelectorConfig;
    }

    public Integer getMinimumSubPillarSize() {
        return minimumSubPillarSize;
    }

    public void setMinimumSubPillarSize(Integer minimumSubPillarSize) {
        this.minimumSubPillarSize = minimumSubPillarSize;
    }

    public Integer getMaximumSubPillarSize() {
        return maximumSubPillarSize;
    }

    public void setMaximumSubPillarSize(Integer maximumSubPillarSize) {
        this.maximumSubPillarSize = maximumSubPillarSize;
    }

    @Override
    public PillarSelectorConfig inherit(PillarSelectorConfig inheritedConfig) {
        entitySelectorConfig = ConfigUtils.inheritConfig(entitySelectorConfig, inheritedConfig.getEntitySelectorConfig());
        minimumSubPillarSize = ConfigUtils.inheritOverwritableProperty(minimumSubPillarSize,
                inheritedConfig.getMinimumSubPillarSize());
        maximumSubPillarSize = ConfigUtils.inheritOverwritableProperty(maximumSubPillarSize,
                inheritedConfig.getMaximumSubPillarSize());
        return this;
    }

    @Override
    public PillarSelectorConfig copyConfig() {
        return new PillarSelectorConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (entitySelectorConfig != null) {
            entitySelectorConfig.visitReferencedClasses(classVisitor);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelectorConfig + ")";
    }
}
