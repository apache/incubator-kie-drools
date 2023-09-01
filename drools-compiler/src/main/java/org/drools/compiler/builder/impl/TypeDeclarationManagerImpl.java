package org.drools.compiler.builder.impl;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.TypeDeclaration;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;

/**
 * Wraps a {@link TypeDeclarationBuilder} and an {@link InternalKnowledgeBase}
 * and deals with updating both.
 */
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
