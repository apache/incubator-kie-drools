package org.kie.api.definition.rule;


import org.drools.core.rule.TypeDeclaration;
import org.kie.api.KieBaseConfiguration;

public interface RuleBase {
    ClassLoader getRootClassLoader();

    TypeDeclaration getOrCreateExactTypeDeclaration(Class<?> nodeClass);

    TypeDeclaration getTypeDeclaration(Class<?> classType);

    KieBaseConfiguration getConfiguration();
}
