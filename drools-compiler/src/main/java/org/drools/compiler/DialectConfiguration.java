package org.drools.compiler;

/**
 * Each Dialect can have its own configuration. Implementations of this class are typically
 * loaded via reflection in PackageBuilderConfiguration during the call to buildDialectRegistry().
 * This Class api is subject to change.
 *
 */
public interface DialectConfiguration {    
    
    public void init(PackageBuilderConfiguration  configuration);
    
    public Dialect getDialect();
    
    public PackageBuilderConfiguration getPackageBuilderConfiguration();
}
