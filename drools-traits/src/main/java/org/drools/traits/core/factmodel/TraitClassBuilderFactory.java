package org.drools.traits.core.factmodel;

import org.drools.compiler.builder.impl.classbuilder.ClassBuilder;
import org.drools.base.rule.TypeDeclaration;
import org.drools.mvel.asm.DefaultClassBuilderFactory;

public class TraitClassBuilderFactory extends DefaultClassBuilderFactory {

    // Trait property wrappers
    private ClassBuilder propertyWrapperBuilder;

    @Override
    public ClassBuilder getPropertyWrapperBuilder() {
        if (propertyWrapperBuilder == null) {
            propertyWrapperBuilder = new TraitMapPropertyWrapperClassBuilderImpl();
        }
        return propertyWrapperBuilder;
    }

    @Override
    public void setPropertyWrapperBuilder(ClassBuilder pcb) {
        propertyWrapperBuilder = pcb;
    }

    // Trait proxy wrappers
    private TraitProxyClassBuilder traitProxyBuilder;

    public TraitProxyClassBuilder getTraitProxyBuilder() {
        if (traitProxyBuilder == null) {
            traitProxyBuilder = new TraitMapProxyClassBuilderImpl();
        }
        return traitProxyBuilder;
    }

    public void setTraitProxyBuilder(TraitProxyClassBuilder tpcb) {
        traitProxyBuilder = tpcb;
    }

    private TraitClassBuilderImpl traitClassBuilder;

    public TraitClassBuilderImpl getTraitClassBuilder() {
        if(traitClassBuilder == null) {
            traitClassBuilder = new TraitClassBuilderImpl();
        }
        return traitClassBuilder;
    }


    @Override
    public ClassBuilder getClassBuilder(TypeDeclaration type) {
        switch (type.getKind()) {
            case TRAIT: return getTraitClassBuilder();
            case ENUM: return getEnumClassBuilder();
            case CLASS: default: return getBeanClassBuilder();
        }
    }

    @Override
    public int servicePriority() {
        return 1;
    }
}
