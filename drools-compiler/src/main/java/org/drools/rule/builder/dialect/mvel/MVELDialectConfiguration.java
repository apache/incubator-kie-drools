package org.drools.rule.builder.dialect.mvel;

import org.drools.compiler.Dialect;
import org.drools.compiler.DialectConfiguration;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;

/**
 * The MVEL dialect.
 * 
 * drools.dialect.mvel.strict = <true|false>
 * 
 * Default strict is true, which means all expressions and the consequence are type safe.
 * However dynamic mode is still used while executed nested accessors in the field constraints.
 *
 */
public class MVELDialectConfiguration
    implements
    DialectConfiguration {

    private MVELDialect                 dialect;
    private PackageBuilderConfiguration conf;

    private boolean                     strict;

    public Dialect getDialect() {
        if ( this.dialect == null ) {
            this.dialect = new MVELDialect();
        }
        return this.dialect;
    }

    public void init(PackageBuilderConfiguration conf) {
        this.conf = conf;
        setStrict( getStrict() );
    }

    public PackageBuilderConfiguration getPackageBuilderConfiguration() {
        return this.conf;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public boolean isStrict() {
        return this.strict;
    }

    private boolean getStrict() {
        final String prop = this.conf.getChainedProperties().getProperty( "drools.dialect.mvel.strict",
                                                                          "true" );
        return Boolean.valueOf( prop ).booleanValue();

    }

}
