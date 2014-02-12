package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.definitions.InternalKnowledgePackage;

/**
 * Each Dialect can have its own configuration. Implementations of this class are typically
 * loaded via reflection in PackageBuilderConfiguration during the call to buildDialectRegistry().
 * This Class api is subject to change.
 */
public interface DialectConfiguration {
    
    public void init(KnowledgeBuilderConfigurationImpl configuration);
    
    public Dialect newDialect(ClassLoader rootClassLoader,
                              KnowledgeBuilderConfigurationImpl pkgConf,
                              PackageRegistry pkgRegistry,
                              InternalKnowledgePackage pkg);
    
    public KnowledgeBuilderConfigurationImpl getPackageBuilderConfiguration();
}
