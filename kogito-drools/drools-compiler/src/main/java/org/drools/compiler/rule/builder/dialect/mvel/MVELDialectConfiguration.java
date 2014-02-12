package org.drools.compiler.rule.builder.dialect.mvel;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectConfiguration;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;

/**
 * The MVEL dialect.
 * 
 * drools.dialect.mvel.strict = <true|false>
 * 
 * Default strict is true, which means all expressions and the consequence are type safe.
 * However dynamic mode is still used while executed nested accessors in the field constraints.
 */
public class MVELDialectConfiguration
    implements
        DialectConfiguration {

    private KnowledgeBuilderConfigurationImpl conf;

    private boolean                     strict;
    
    private int                         langLevel;

    public Dialect newDialect(ClassLoader rootClassLoader, KnowledgeBuilderConfigurationImpl pkgConf, PackageRegistry pkgRegistry, InternalKnowledgePackage pkg) {
        return new MVELDialect(rootClassLoader, pkgConf, pkgRegistry, pkg);
    }

    public void init(KnowledgeBuilderConfigurationImpl conf) {
        this.conf = conf;
        setStrict( determineStrict() );
        setLangLevel( determineLangLevel() );
    }

    public KnowledgeBuilderConfigurationImpl getPackageBuilderConfiguration() {
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
