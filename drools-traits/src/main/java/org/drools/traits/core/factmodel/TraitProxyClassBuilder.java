package org.drools.traits.core.factmodel;

import org.drools.compiler.builder.impl.classbuilder.ClassBuilder;
import org.drools.base.factmodel.ClassDefinition;
import org.mvel2.asm.Opcodes;

public interface TraitProxyClassBuilder extends ClassBuilder, Opcodes {

    void init( ClassDefinition trait, Class<?> proxyBaseClass, TraitRegistryImpl traitRegistryImpl);

}
