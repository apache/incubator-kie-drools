package org.drools.rule.builder.dialect.mvel;

import org.drools.compiler.Dialect;
import org.drools.compiler.DialectConfiguration;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageRegistry;
import org.drools.rule.Package;

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

    private PackageBuilderConfiguration conf;

    private boolean                     strict;
    
    private int                         langLevel;

    public Dialect newDialect(PackageBuilder packageBuilder, PackageRegistry pkgRegistry, Package pkg) {
        return new MVELDialect(packageBuilder,
                               pkgRegistry,
                               pkg);
    }

    public void init(PackageBuilderConfiguration conf) {
        this.conf = conf;
        setStrict( determineStrict() );
        setLangLevel( determineLangLevel() );
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
    
    public void setLangLevel(int langLevel) {
        this.langLevel = langLevel;
    }
    
    public int getLangLevel() {
        return this.langLevel;
    }

    private boolean determineStrict() {
        final String prop = this.conf.getChainedProperties().getProperty( "drools.dialect.mvel.strict",
                                                                          "true" );
        return Boolean.valueOf( prop ).booleanValue();
    }
    
    private int determineLangLevel() {
        final String prop = this.conf.getChainedProperties().getProperty( "drools.dialect.mvel.langLevel",
                                                                          "4" );
        return Integer.valueOf( prop ).intValue();
    }    

}
