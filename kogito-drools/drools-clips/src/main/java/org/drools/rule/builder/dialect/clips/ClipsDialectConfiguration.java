package org.drools.rule.builder.dialect.clips;

import org.drools.compiler.Dialect;
import org.drools.compiler.DialectConfiguration;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;

/**
 * The Clips dialect.
 * 
 *
 */
public class ClipsDialectConfiguration
    implements
    DialectConfiguration {

    private ClipsDialect                dialect;
    private PackageBuilderConfiguration conf;

    public Dialect getDialect() {
        if ( this.dialect == null ) {
            this.dialect = new ClipsDialect();
        }
        return this.dialect;
    }

    public void init(PackageBuilderConfiguration conf) {
        this.conf = conf;
    }

    public PackageBuilderConfiguration getPackageBuilderConfiguration() {
        return this.conf;
    }

}
