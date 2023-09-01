package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.base.definitions.InternalKnowledgePackage;

/**
 * Each Dialect can have its own configuration. Implementations of this class are typically
 * loaded via reflection in PackageBuilderConfiguration during the call to buildDialectRegistry().
 * This Class api is subject to change.
 */
public interface DialectConfiguration {
    
    void init(KnowledgeBuilderConfigurationImpl configuration);
    
    Dialect newDialect(ClassLoader rootClassLoader,
                       KnowledgeBuilderConfigurationImpl pkgConf,
                       PackageRegistry pkgRegistry,
                       InternalKnowledgePackage pkg);
    
    KnowledgeBuilderConfigurationImpl getPackageBuilderConfiguration();
}
