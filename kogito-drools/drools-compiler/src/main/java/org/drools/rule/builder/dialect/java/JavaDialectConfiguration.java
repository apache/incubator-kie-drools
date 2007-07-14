package org.drools.rule.builder.dialect.java;

import java.util.Arrays;
import java.util.Properties;

import org.drools.RuntimeDroolsException;
import org.drools.compiler.Dialect;
import org.drools.compiler.DialectConfiguration;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;

public class JavaDialectConfiguration
    implements
    DialectConfiguration {
    public static final int             ECLIPSE         = 0;
    public static final int             JANINO          = 1;

    public static final String[]        LANGUAGE_LEVELS = new String[]{"1.4", "1.5", "1.6"};

    private String                      languageLevel;

    private PackageBuilderConfiguration conf;

    private int                         compiler;

    private JavaDialect                 dialect;

    public JavaDialectConfiguration() {
    }

    public void init(final PackageBuilderConfiguration conf) {
        this.conf = conf;

        setCompiler( getDefaultCompiler() );
        setJavaLanguageLevel( getDefaultLanguageLevel() );
    }

    public PackageBuilderConfiguration getPackageBuilderConfiguration() {
        return this.conf;
    }

    public Dialect getDialect() {
        if ( this.dialect == null ) {
            this.dialect = new JavaDialect();
        }
        return this.dialect;
    }

    public String getJavaLanguageLevel() {
        return this.languageLevel;
    }

    /**
     * You cannot set language level below 1.5, as we need static imports, 1.5 is now the default.
     * @param level
     */
    public void setJavaLanguageLevel(final String languageLevel) {
        if ( Arrays.binarySearch( LANGUAGE_LEVELS,
                                  languageLevel ) < 0 ) {
            throw new RuntimeDroolsException( "value '" + languageLevel + "' is not a valid language level" );
        }
        this.languageLevel = languageLevel;
    }

    /** 
     * Set the compiler to be used when building the rules semantic code blocks.
     * This overrides the default, and even what was set as a system property. 
     */
    public void setCompiler(final int compiler) {
        switch ( compiler ) {
            case JavaDialectConfiguration.ECLIPSE :
                this.compiler = JavaDialectConfiguration.ECLIPSE;
                break;
            case JavaDialectConfiguration.JANINO :
                this.compiler = JavaDialectConfiguration.JANINO;
                break;
            default :
                throw new RuntimeDroolsException( "value '" + compiler + "' is not a valid compiler" );
        }
    }

    public int getCompiler() {
        return this.compiler;
    }

    /**
     * This will attempt to read the System property to work out what default to set.
     * This should only be done once when the class is loaded. After that point, you will have
     * to programmatically override it.
     */
    private int getDefaultCompiler() {
        try {
            final String prop = this.conf.getChainedProperties().getProperty( "drools.dialect.java.compiler",
                                                                              "ECLIPSE" );
            if ( prop.equals( "ECLIPSE".intern() ) ) {
                return JavaDialectConfiguration.ECLIPSE;
            } else if ( prop.equals( "JANINO" ) ) {
                return JavaDialectConfiguration.JANINO;
            } else {
                System.err.println( "Drools config: unable to use the drools.compiler property. Using default. It was set to:" + prop );
                return JavaDialectConfiguration.ECLIPSE;
            }
        } catch ( final SecurityException e ) {
            System.err.println( "Drools config: unable to read the drools.compiler property. Using default." );
            return JavaDialectConfiguration.ECLIPSE;
        }
    }

    private String getDefaultLanguageLevel() {
        String level = this.conf.getChainedProperties().getProperty( "drools.dialect.java.compiler.lnglevel",
                                                                     null );

        if ( level == null ) {
            String version = System.getProperty( "java.version" );
            if ( version.startsWith( "1.4" ) ) {
                level = "1.4";
            } else if ( version.startsWith( "1.5" ) ) {
                level = "1.5";
            } else if ( version.startsWith( "1.6" ) ) {
                level = "1.6";
            } else {
                level = "1.4";
            }
        }

        if ( Arrays.binarySearch( LANGUAGE_LEVELS,
                                  level ) < 0 ) {
            throw new RuntimeDroolsException( "value '" + level + "' is not a valid language level" );
        }

        return level;
    }

}
