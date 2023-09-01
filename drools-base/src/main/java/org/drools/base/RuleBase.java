package org.drools.base;


import org.drools.base.rule.TypeDeclaration;
import org.kie.api.KieBaseConfiguration;

public interface RuleBase {
    ClassLoader getRootClassLoader();

    TypeDeclaration getOrCreateExactTypeDeclaration(Class<?> nodeClass);

    TypeDeclaration getTypeDeclaration(Class<?> classType);

    KieBaseConfiguration getConfiguration();
}
