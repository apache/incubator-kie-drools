package org.drools.compiler.builder.impl;

public class DefaultTypeDeclarationBuilderFactory implements TypeDeclarationBuilderFactory {

    @Override
    public TypeDeclarationBuilder createTypeDeclarationBuilder(KnowledgeBuilderImpl kbuilder) {
        return new TypeDeclarationBuilder(kbuilder, kbuilder);
    }
}
