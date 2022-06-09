package org.optaplanner.core.config.heuristic.selector.move.factory;

import java.util.Map;
import java.util.function.Consumer;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.io.jaxb.adapter.JaxbCustomPropertiesAdapter;

@XmlType(propOrder = {
        "moveIteratorFactoryClass",
        "moveIteratorFactoryCustomProperties"
})
public class MoveIteratorFactoryConfig extends MoveSelectorConfig<MoveIteratorFactoryConfig> {

    public static final String XML_ELEMENT_NAME = "moveIteratorFactory";

    protected Class<? extends MoveIteratorFactory> moveIteratorFactoryClass = null;

    @XmlJavaTypeAdapter(JaxbCustomPropertiesAdapter.class)
    protected Map<String, String> moveIteratorFactoryCustomProperties = null;

    public Class<? extends MoveIteratorFactory> getMoveIteratorFactoryClass() {
        return moveIteratorFactoryClass;
    }

    public void setMoveIteratorFactoryClass(Class<? extends MoveIteratorFactory> moveIteratorFactoryClass) {
        this.moveIteratorFactoryClass = moveIteratorFactoryClass;
    }

    public Map<String, String> getMoveIteratorFactoryCustomProperties() {
        return moveIteratorFactoryCustomProperties;
    }

    public void setMoveIteratorFactoryCustomProperties(Map<String, String> moveIteratorFactoryCustomProperties) {
        this.moveIteratorFactoryCustomProperties = moveIteratorFactoryCustomProperties;
    }

    @Override
    public MoveIteratorFactoryConfig inherit(MoveIteratorFactoryConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        moveIteratorFactoryClass = ConfigUtils.inheritOverwritableProperty(
                moveIteratorFactoryClass, inheritedConfig.getMoveIteratorFactoryClass());
        moveIteratorFactoryCustomProperties = ConfigUtils.inheritMergeableMapProperty(
                moveIteratorFactoryCustomProperties, inheritedConfig.getMoveIteratorFactoryCustomProperties());
        return this;
    }

    @Override
    public MoveIteratorFactoryConfig copyConfig() {
        return new MoveIteratorFactoryConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        visitCommonReferencedClasses(classVisitor);
        classVisitor.accept(moveIteratorFactoryClass);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + moveIteratorFactoryClass + ")";
    }

}
