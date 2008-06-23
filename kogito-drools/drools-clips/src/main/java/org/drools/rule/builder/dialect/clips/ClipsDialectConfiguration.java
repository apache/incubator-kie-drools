package org.drools.rule.builder.dialect.clips;

import org.drools.compiler.Dialect;
import org.drools.compiler.DialectConfiguration;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageRegistry;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.rule.builder.dialect.mvel.MVELDialectConfiguration;

/**
 * The Clips dialect.
 * 
 *
 */
public class ClipsDialectConfiguration
    extends MVELDialectConfiguration {
    
    public Dialect newDialect(PackageBuilder packageBuilder, PackageRegistry pkgRegistry, Package pkg) {
        return new ClipsDialect(packageBuilder, pkgRegistry, pkg);
    }    

}
