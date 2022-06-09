package org.optaplanner.core.config.heuristic.selector.move.factory;

import java.util.Map;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.core.impl.io.jaxb.adapter.JaxbCustomPropertiesAdapter;

@XmlType(propOrder = {
        "moveListFactoryClass",
        "moveListFactoryCustomProperties"
})
public class MoveListFactoryConfig extends MoveSelectorConfig<MoveListFactoryConfig> {

    public static final String XML_ELEMENT_NAME = "moveListFactory";

    protected Class<? extends MoveListFactory> moveListFactoryClass = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    protected Map<String, String> moveListFactoryCustomProperties = null;

    public Class<? extends MoveListFactory> getMoveListFactoryClass() {
        return moveListFactoryClass;
    }

    public void setMoveListFactoryClass(Class<? extends MoveListFactory> moveListFactoryClass) {
        this.moveListFactoryClass = moveListFactoryClass;
    }

    public Map<String, String> getMoveListFactoryCustomProperties() {
        return moveListFactoryCustomProperties;
    }

    public void setMoveListFactoryCustomProperties(Map<String, String> moveListFactoryCustomProperties) {
        this.moveListFactoryCustomProperties = moveListFactoryCustomProperties;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public MoveListFactoryConfig inherit(MoveListFactoryConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        moveListFactoryClass = ConfigUtils.inheritOverwritableProperty(
                moveListFactoryClass, inheritedConfig.getMoveListFactoryClass());
        moveListFactoryCustomProperties = ConfigUtils.inheritMergeableMapProperty(
                moveListFactoryCustomProperties, inheritedConfig.getMoveListFactoryCustomProperties());
        return this;
    }

    @Override
    public MoveListFactoryConfig copyConfig() {
        return new MoveListFactoryConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        classVisitor.accept(moveListFactoryClass);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + moveListFactoryClass + ")";
    }

}
