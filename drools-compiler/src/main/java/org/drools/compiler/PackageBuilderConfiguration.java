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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.drools.RuntimeDroolsException;
import org.drools.rule.builder.Dialect;
import org.drools.util.ChainedProperties;

/**
 * This class configures the package compiler. 
 * There are options to use various flavours of runtime compilers.
 * Apache JCI is used as the interface to all the runtime compilers.
 * You may also use this class to override the class loader defaults that are otherwise used.
 * Normally you will not need to look at this class, unless you want to override the defaults.
 * 
 * You can also use the system property "drools.compiler" to set the desired compiler.
 * The valid values are "ECLIPSE" and "JANINO" only. 
 * s
 * The default Java language level is 1.4 but it can be configured using the 
 * system property "drools.compiler.lnglevel". Valid values are 1.4, 1.5 and 1.6.
 */
public class PackageBuilderConfiguration {
    public static final int      ECLIPSE         = 0;
    public static final int      JANINO          = 1;

    public static final String[] LANGUAGE_LEVELS = new String[]{"1.4", "1.5", "1.6"};

    private Map                  dialects;

    private String               defaultDialect;

    private int                  compiler;

//    private ClassLoader          classLoader;

    private String               languageLevel;

    private ChainedProperties    chainedProperties;

    /**
     * Programmatic properties file, added with lease precedence
     * @param properties
     */
    public PackageBuilderConfiguration(Properties properties) {
        init( null,
              properties );
    }

    /**
     * Programmatic properties file, added with lease precedence
     * @param classLoader
     * @param properties
     */
    public PackageBuilderConfiguration(ClassLoader classLoader,
                                       Properties properties) {
        init( classLoader,
              properties );
    }

    public PackageBuilderConfiguration() {
        init( null,
              null );
    }

//    public PackageBuilderConfiguration(ClassLoader classLoader) {
//        init( classLoader,
//              null );
//    }

    private void init(ClassLoader classLoader,
                      Properties properties) {
        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = this.getClass().getClassLoader();
            }
            //setClassLoader( classLoader );
        }
        //setClassLoader( classLoader );

        this.chainedProperties = new ChainedProperties( Thread.currentThread().getContextClassLoader(),
                                                        "packagebuilder.conf" );

        if ( properties != null ) {
            this.chainedProperties.addProperties( properties );
        }

        setJavaLanguageLevel( getDefaultLanguageLevel() );

        setCompiler( getDefaultCompiler() );

        this.dialects = new HashMap();
        this.chainedProperties.mapStartsWith( this.dialects,
                                              "drools.dialect" );
        setDefaultDialect( (String) this.dialects.remove( "drools.dialect.default" ) );
    }

    public int getCompiler() {
        return this.compiler;
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

    public DialectRegistry buildDialectRegistry(PackageBuilder packageBuilder) {
        DialectRegistry registry = new DialectRegistry();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for ( Iterator it = this.dialects.entrySet().iterator(); it.hasNext(); ) {
            Entry entry = (Entry) it.next();
            String str = (String) entry.getKey();
            String dialectName = str.substring( str.lastIndexOf( "." ) + 1 );
            String dialectClass = (String) entry.getValue();
            try {
                Class cls = classLoader.loadClass( dialectClass );
                Constructor cons = cls.getConstructor( new Class[] { PackageBuilder.class } );
                registry.addDialect( dialectName,
                                     (Dialect) cons.newInstance( new Object[] { packageBuilder } ) );
            } catch ( Exception e ) {
                throw new RuntimeDroolsException( "Unable to load dialect '" + dialectClass + ":" + dialectName + "'" );
            }
        }
        return registry;
    }

    public String getDefaultDialect() {
        return this.defaultDialect;
    }

    public void setDefaultDialect(String defaultDialect) {
        this.defaultDialect = defaultDialect;
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

//    public ClassLoader getClassLoader() {
//        return this.classLoader;
//    }
//
//    /** Use this to override the classloader that will be used for the rules. */
//    public void setClassLoader(final ClassLoader classLoader) {
//        if ( classLoader != null ) {
//            this.classLoader = classLoader;
//        }
//    }

    /**
     * This will attempt to read the System property to work out what default to set.
     * This should only be done once when the class is loaded. After that point, you will have
     * to programmatically override it.
     */
    private int getDefaultCompiler() {
        try {
            final String prop = this.chainedProperties.getProperty( "drools.compiler",
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

    private String getDefaultLanguageLevel() {
        String level = this.chainedProperties.getProperty( "drools.compiler.lnglevel",
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