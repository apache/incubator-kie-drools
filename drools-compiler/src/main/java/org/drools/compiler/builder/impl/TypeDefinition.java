package org.drools.compiler.builder.impl;

import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.core.rule.TypeDeclaration;

class TypeDefinition {

    final AbstractClassTypeDeclarationDescr typeDescr;
    final TypeDeclaration type;

    TypeDefinition( TypeDeclaration type,
                    AbstractClassTypeDeclarationDescr typeDescr ) {
        this.type = type;
        this.typeDescr = typeDescr;
    }

    public String getTypeClassName() {
        return type.getTypeClassName();
    }

    public String getNamespace() {
        return typeDescr.getNamespace();
    }
}