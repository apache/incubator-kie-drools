package org.drools.mvel.asm;

import java.io.Serializable;

import org.drools.compiler.builder.impl.classbuilder.BeanClassBuilder;
import org.drools.compiler.builder.impl.classbuilder.ClassBuilder;
import org.drools.compiler.builder.impl.classbuilder.ClassBuilderFactory;
import org.drools.compiler.builder.impl.classbuilder.EnumClassBuilder;
import org.drools.base.rule.TypeDeclaration;

public class DefaultClassBuilderFactory implements Serializable, ClassBuilderFactory {

    // Generic beans
    private BeanClassBuilder beanClassBuilder = new DefaultBeanClassBuilder(true);

    @Override
    public ClassBuilder getBeanClassBuilder() {
        return beanClassBuilder;
    }

    private EnumClassBuilder enumClassBuilder = new DefaultEnumClassBuilder();

    @Override
    public EnumClassBuilder getEnumClassBuilder() {
        return enumClassBuilder;
    }

    @Override
    public ClassBuilder getPropertyWrapperBuilder() {
        return null;
    }

    @Override
    public void setPropertyWrapperBuilder(ClassBuilder pcb) {

    }

    @Override
    public ClassBuilder getClassBuilder(TypeDeclaration type) {
        switch (type.getKind()) {
            case ENUM: return getEnumClassBuilder();
            case CLASS: default: return getBeanClassBuilder();
        }
    }
}
