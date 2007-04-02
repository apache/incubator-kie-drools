package org.drools.compiler;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Arrays;

import org.drools.RuntimeDroolsException;

/**
 * This class configures the package compiler. 
 * There are options to use various flavours of runtime compilers.
 * Apache JCI is used as the interface to all the runtime compilers.
 * You may also use this class to override the class loader defaults that are otherwise used.
 * Normally you will not need to look at this class, unless you want to override the defaults.
 * 
 * You can also use the system property "drools.compiler" to set the desired compiler.
 * The valid values are "ECLIPSE" and "JANINO" only. 
 * 
 * The default Java language level is 1.4 but it can be configured using the 
 * system property "drools.compiler.lnglevel". Valid values are 1.4, 1.5 and 1.6.
 */
public class PackageBuilderConfiguration {
    public static final int      ECLIPSE                   = 0;
    public static final int      JANINO                    = 1;

    public static final String[] LANGUAGE_LEVELS           = new String[]{"1.4", "1.5", "1.6"};
    public static final String   DEFAULT_LANGUAGE_LEVEL    = "1.4";

    /** These will be only setup once. It tries to look for a system property */
    private static final int     CONFIGURED_COMPILER       = getDefaultCompiler();
    private static final String  CONFIGURED_LANGUAGE_LEVEL = getDefaultLanguageLevel();

    private int                  compiler                  = PackageBuilderConfiguration.CONFIGURED_COMPILER;

    private ClassLoader          classLoader;

    private String               languageLevel             = PackageBuilderConfiguration.CONFIGURED_LANGUAGE_LEVEL;

    public PackageBuilderConfiguration() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if ( classLoader == null ) {
            classLoader = this.getClass().getClassLoader();
        }
        this.classLoader = classLoader;
    }

    public int getCompiler() {
        return this.compiler;
    }

    public String getJavaLanguageLevel() {
        if ( languageLevel != null ) return languageLevel;
        setJavaLanguageLevel( System.getProperty( "drools.compiler.lnglevel",
                                                  DEFAULT_LANGUAGE_LEVEL ) );
        return languageLevel;
    }

    /**
     * You cannot set language level below 1.5, as we need static imports, 1.5 is now the default.
     * @param level
     */
    public void setJavaLanguageLevel(String level) {
        if ( Arrays.binarySearch( LANGUAGE_LEVELS,
                                  languageLevel ) < 0 ) throw new RuntimeDroolsException( "value '" + languageLevel + "' is not a valid language level" );
        languageLevel = level;
    }

    /** 
     * Set the compiler to be used when building the rules semantic code blocks.
     * This overrides the default, and even what was set as a system property. 
     */
    public void setCompiler(final int compiler) {
        switch ( compiler ) {
            case PackageBuilderConfiguration.ECLIPSE :
                this.compiler = PackageBuilderConfiguration.ECLIPSE;
                break;
            case PackageBuilderConfiguration.JANINO :
                this.compiler = PackageBuilderConfiguration.JANINO;
                break;
            default :
                throw new RuntimeDroolsException( "value '" + compiler + "' is not a valid compiler" );
        }
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /** Use this to override the classloader that will be used for the rules. */
    public void setClassLoader(final ClassLoader classLoader) {
        if ( classLoader != null ) {
            this.classLoader = classLoader;
        }
    }

    /**
     * This will attempt to read the System property to work out what default to set.
     * This should only be done once when the class is loaded. After that point, you will have
     * to programmatically override it.
     */
    static int getDefaultCompiler() {
        try {
            final String prop = System.getProperty( "drools.compiler",
                                                    "ECLIPSE" );
            if ( prop.equals( "ECLIPSE".intern() ) ) {
                return PackageBuilderConfiguration.ECLIPSE;
            } else if ( prop.equals( "JANINO" ) ) {
                return PackageBuilderConfiguration.JANINO;
            } else {
                System.err.println( "Drools config: unable to use the drools.compiler property. Using default. It was set to:" + prop );
                return PackageBuilderConfiguration.ECLIPSE;
            }
        } catch ( final SecurityException e ) {
            System.err.println( "Drools config: unable to read the drools.compiler property. Using default." );
            return PackageBuilderConfiguration.ECLIPSE;
        }
    }

    static String getDefaultLanguageLevel() {
        try {
            String languageLevel = System.getProperty( "drools.compiler.languagelevel",
                                                       DEFAULT_LANGUAGE_LEVEL );

            if ( Arrays.binarySearch( LANGUAGE_LEVELS,
                                      languageLevel ) < 0 ) {
                throw new RuntimeDroolsException( "value '" + languageLevel + "' is not a valid language level" );
            }

            return languageLevel;
        } catch ( Exception e ) {
            System.err.println( "Drools config: unable to read the drools.compiler.lnglevel property. Using default." );
            return DEFAULT_LANGUAGE_LEVEL;
        }
    }
}