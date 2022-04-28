package org.drools.compiler.builder.impl;

import org.drools.core.base.ClassObjectType;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;

public class TypeDeclarationManagerImpl implements TypeDeclarationManager {

    private final TypeDeclarationBuilder typeBuilder;
    private final InternalKnowledgeBase kBase;

    public TypeDeclarationManagerImpl(TypeDeclarationBuilder typeBuilder, InternalKnowledgeBase kBase) {
        this.typeBuilder = typeBuilder;
        this.kBase = kBase;
    }

    public TypeDeclarationBuilder getTypeDeclarationBuilder() {
        return typeBuilder;
    }

    @Override
    public TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String packageName) {
        if (kBase != null) {
            InternalKnowledgePackage pkg = kBase.getPackage(packageName);
            if (pkg != null) {
                TypeDeclaration typeDeclaration = pkg.getTypeDeclaration(cls);
                if (typeDeclaration != null) {
                    return typeDeclaration;
                }
            }
        }
        return typeBuilder.getAndRegisterTypeDeclaration(cls, packageName);
    }


    @Override
    public TypeDeclaration getTypeDeclaration(Class<?> cls) {
        return cls != null ? typeBuilder.getTypeDeclaration(cls) : null;
    }

    @Override
    public TypeDeclaration getTypeDeclaration(ObjectType objectType) {
        return objectType.isTemplate() ?
                typeBuilder.getExistingTypeDeclaration(objectType.getClassName()) :
                typeBuilder.getTypeDeclaration(((ClassObjectType) objectType).getClassType());
    }

}
