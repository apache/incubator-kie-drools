package org.drools.compiler.builder.impl;

import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;

public interface TypeDeclarationManager {
    TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String packageName);

    TypeDeclaration getTypeDeclaration(Class<?> cls);

    TypeDeclaration getTypeDeclaration(ObjectType objectType);
}
