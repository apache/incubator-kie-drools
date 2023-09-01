package org.drools.compiler.builder.impl;

import org.drools.base.base.ObjectType;
import org.drools.base.rule.TypeDeclaration;

/**
 * Public interface to a {@link TypeDeclarationBuilder}
 *
 * Deals with updating a {@link org.kie.api.KieBase}, if there exist a live one.
 *
 */
public interface TypeDeclarationManager {
    TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String packageName);

    TypeDeclaration getTypeDeclaration(Class<?> cls);

    TypeDeclaration getTypeDeclaration(ObjectType objectType);
}
