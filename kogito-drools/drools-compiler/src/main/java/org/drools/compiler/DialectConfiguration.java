package org.drools.compiler;

public interface DialectConfiguration {    
    
    public void init(PackageBuilderConfiguration  configuration);
    
    public Dialect getDialect();
    
    public PackageBuilderConfiguration getPackageBuilderConfiguration();
}
