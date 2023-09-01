package org.drools.traits.compiler.builder.impl;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDeclarationBuilderFactory;

public class TraitTypeDeclarationBuilderFactory implements TypeDeclarationBuilderFactory {

    public TraitTypeDeclarationBuilderFactory() {
    }

    @Override
    public TypeDeclarationBuilder createTypeDeclarationBuilder(KnowledgeBuilderImpl kbuilder) {
        return new TraitsTypeDeclarationBuilderImpl(kbuilder);
    }
}
